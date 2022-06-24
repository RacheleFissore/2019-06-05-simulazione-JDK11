package it.polito.tdp.crimes.model;

public class Adiacenza {
	private Integer vertice1;
	private Integer vertice2;
	private Double peso;
	
	public Adiacenza(Integer vertice1, Integer vertice2, Double peso) {
		super();
		this.vertice1 = vertice1;
		this.vertice2 = vertice2;
		this.peso = peso;
	}

	public Integer getVertice1() {
		return vertice1;
	}

	public void setVertice1(Integer vertice1) {
		this.vertice1 = vertice1;
	}

	public Integer getVertice2() {
		return vertice2;
	}

	public void setVertice2(Integer vertice2) {
		this.vertice2 = vertice2;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}
}
