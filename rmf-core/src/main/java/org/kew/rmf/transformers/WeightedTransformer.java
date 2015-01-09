/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.transformers;

/**
 * A WeightedTransformer adds a weight (score value) and an order to a
 * {@link Transformer}.
 */
public class WeightedTransformer implements Transformer {
	private Transformer transformer;
	private int order;
	private double weight;

	/* ••• Methods in Transformer ••• */
	@Override
	public String transform(String s) throws TransformationException {
		return transformer.transform(s);
	}

	/* ••• Constructors ••• */
	public WeightedTransformer() {
	}

	public WeightedTransformer(Transformer transformer, double weight, int order) {
		this.transformer = transformer;
		this.weight = weight;
		this.order = order;
	}

	/* ••• Getters and setters ••• */
	public Transformer getTransformer() {
		return transformer;
	}
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
