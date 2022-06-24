package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento> {
	public enum EventType {
		CRIMINE,
		ARRIVA_AGENTE,
		GESTITO
	}
	
	private EventType type;
	private LocalDateTime data;
	private Event crimine;
	
	public Evento(EventType type, LocalDateTime data, Event event) {
		super();
		this.type = type;
		this.data = data;
		this.crimine = event;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Event getCrimine() {
		return crimine;
	}

	public void setCrimine(Event event) {
		this.crimine = event;
	}

	@Override
	public int compareTo(Evento o) {
		return this.data.compareTo(o.getData());
	}
	
}
