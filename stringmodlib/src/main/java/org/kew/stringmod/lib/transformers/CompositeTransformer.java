package org.kew.stringmod.lib.transformers;

import java.util.List;

public class CompositeTransformer implements Transformer{

	private List<Transformer> transformers;

	public String transform(String s) throws Exception {
		for (Transformer t : transformers)
			s = t.transform(s);
		return s;
	}

	public List<Transformer> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<Transformer> transformers) {
		this.transformers = transformers;
	}
		
}
