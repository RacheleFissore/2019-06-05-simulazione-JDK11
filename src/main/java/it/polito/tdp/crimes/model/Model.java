package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private EventsDao dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	
	
	public Model() {
		dao = new EventsDao();	
	}
	
	public List<Integer> getAnni() {
		return dao.anniCrimes();
	}
	
	public List<Integer> getMesi() {
		return dao.mesiCrimes();
	}
	
	public List<Integer> getGiorni() {
		return dao.giorniCrimes();
	}
	
	public void creaGrafo(int anno) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici());
		
		for(Integer v1 : grafo.vertexSet()) {
			for(Integer v2 : grafo.vertexSet()) {
				Double latV1 = dao.latMedia(v1, anno);
				Double latV2 = dao.latMedia(v2, anno);
				Double longV1 = dao.longMedia(v1, anno);
				Double longV2 = dao.longMedia(v2, anno);
				
				LatLng centro1 = new LatLng(latV1, longV1);
				LatLng centro2 = new LatLng(latV2, longV2);
				Double peso = LatLngTool.distance(centro1, centro2, LengthUnit.KILOMETER);
				
				if(!v1.equals(v2) && !grafo.containsEdge(grafo.getEdge(v1, v2))) {
					Graphs.addEdgeWithVertices(grafo, v1, v2, peso);
					
				}
					
			}
		}
		
	}
	
	public Integer getNVertici() {
		return grafo.vertexSet().size();
	}
	 
	public Integer getNArchi() {
		return grafo.edgeSet().size();
	}
	
	public String viciniAdiacenti() {
		String string = "";
		for(Integer vp : grafo.vertexSet()) {
			string += "Vertice partenza: " + vp + "\n";
			for(Adiacenza vicino : getVicini(vp)) {
				string += vicino.getVertice2() + " (" + vicino.getPeso() + ")\n";
			}
			string += "\n";
		}
		
		return string;
	}
	
	public List<Adiacenza> getVicini(Integer vp) {
		List<Adiacenza> adiacenze = new ArrayList<>();
		List<Integer> vicini = Graphs.neighborListOf(grafo, vp);
		
		for(Integer v : vicini) {
			adiacenze.add(new Adiacenza(vp, v, grafo.getEdgeWeight(grafo.getEdge(vp, v))));
		}
		
		Collections.sort(adiacenze, new Comparator<Adiacenza>() {
			@Override
			public int compare(Adiacenza o1, Adiacenza o2) {
				return o1.getPeso().compareTo(o2.getPeso());
			}			
		});
		
		return adiacenze;
		
	}
	
	public int simula(Integer anno, Integer mese, Integer giorno, Integer N) {
		Simulatore simulatore = new Simulatore();
		simulatore.init(N, anno, mese, giorno, grafo);
		return simulatore.run();
	}
}
