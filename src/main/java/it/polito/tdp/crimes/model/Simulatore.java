package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.EventType;

public class Simulatore {
	// Tipi di evento
	// 1) Evento criminoso (modellato dalla classe Event) 
	// 	  1.1 La centrale seleziona l'agente libero più vicino
	// 	  1.2 Se non c'è nessuno disponibile in quel momento l'evento è MAL GESTITO
	//	  1.3 Se c'è un agente libero allora setto l'agente ad occupato
	// 2) L'agente selezionato ARRIVA sul posto
	//    2.1 Definisco quanto durerà l'intervento e sulla base di questo modelleremo un altro evento che determinerà la fine del crimine
	//    2.2 Controllo se il crimine è MAL GESTITO se l'agente arriva con 15 minuti di ritardo
	// 3) Il crimine è terminato
	//    3.1 Bisogna settare l'agente come non più occupato, cioè l'agente torna ad essere disponibile
	
	// Strutture dati
	// Input utente
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	
	// Stato del sistema
	private Graph<Integer, DefaultWeightedEdge> grafo;
	// Uso una mappa che come key ha il numero del distretto e come value ha il numero di agenti disponibili nel distretto
	private Map<Integer, Integer> agenti; // mappa distretto -> numero agenti liberi
	
	// Coda degli eventi
	private PriorityQueue<Evento> queue;
	
	//Output
	private Integer malGestiti;
	
	public void init(Integer N, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;
		malGestiti = 0;
		agenti = new HashMap<>();
		for(Integer d : grafo.vertexSet()) {
			// I vertici sono i distretti quindi per ogni distretto metto che all'inizio ci sono 0 agenti
			agenti.put(d, 0);
		}
		
		// Devo scegliere dov'è la centrale e mettere N agenti in quel distretto. Dalla centrale partiranno poi gli agenti per andare nei
		// distretti in cui si verifica un certo evento
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno); // Distretto a minore criminalità nell'anno selezionato dall'utente
		agenti.put(minD, N); // In questo distretto andiamo a mettere gli N agenti inizialmente in centrale
	
		// Creo e inizializzo la coda
		queue = new PriorityQueue<Evento>();
		
		for(Event event : dao.listAllEventsByDate(anno, mese, giorno)) {
			queue.add(new Evento(EventType.CRIMINE, event.getReported_date(), event));
		}
	}
	
	public int run() {
		Evento e;
		while((e = queue.poll()) != null) {
			switch (e.getType()) {
				case CRIMINE:
					System.out.println("NUOVO CRIMINE! " + e.getCrimine().getIncident_id());
					
					// Cerco l'agente libero più vicino al distretto in cui si è verificato il crimine
					Integer partenza = null;
					partenza = cercaAgente(e.getCrimine().getDistrict_id()); // Metodo che mi da il distretto da cui partirà l'agente
					if(partenza != null) {
						// C'è un agente libero in partenza -> lo setto come occupato
						agenti.put(partenza, agenti.get(partenza)-1);
					
						// Cerco di capire quanto ci metterà l'agente libero ad arrivare sul posto
						Double distanza;
						if(partenza.equals(e.getCrimine().getDistrict_id())) {
							// Se il distretto di partenza è lo stesso in cui si è verificato il crimine allora la distanza è nulla
							distanza = 0.0;
						} else {
							distanza = grafo.getEdgeWeight(grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
						}
						
						Long seconds = (long)((distanza*1000)/(60/3.6)); // Velocità = spazio/tempo
						queue.add(new Evento(EventType.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
					} else {
						// Non c'è nessun agente libero al momento -> crimine MAL GESTITO
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO");
						malGestiti++;
					}
					break;
	
				case ARRIVA_AGENTE: 
					System.out.println("ARRIVA AGENTE PER CRIMINE! " + e.getCrimine().getIncident_id());
					Long duration = getDurata(e.getCrimine().getOffense_category_id());
					queue.add(new Evento(EventType.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
					
					// Controllo se il crimine è mal gestito, ossia se l'agente arriva con un ritardo di 15 minuti
					if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO");
						malGestiti++;
					}
					break;
					
				case GESTITO:
					System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " GESTITO");
					agenti.put(e.getCrimine().getDistrict_id(), agenti.get(e.getCrimine().getDistrict_id())+1);
					break;
					
				default:
					break;
			}
		}
		
		return malGestiti;
	}

	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random random = new Random();
			if(random.nextDouble() > 0.5) {
				return Long.valueOf(2*60+60);
			}
			else {
				return Long.valueOf(1*60+60);
			}
		}
		else 
			return Long.valueOf(2*60+60);
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE; // Devo trovare il minimo quindi parto da una distanza elevata
		Integer distretto = null;
		
		for(Integer d : agenti.keySet()) {
			// Prima di sovrascrivere la distanza migliore devo chiedermi se ci sono agenti disponibili nel disretto
			if(agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					// La distanza sarà 0 se siamo nello stesso distretto
					distanza = 0.0;
					distretto = d;
				}
				else if(grafo.getEdgeWeight(grafo.getEdge(district_id, d)) < distanza) {
					// Se i due distretti non sono uguali
					distanza = grafo.getEdgeWeight(grafo.getEdge(district_id, d));
					distretto = d;
				}
			} 
		}
		return distretto;
	}
}
