package org.kew.rmf.refine.domain.metadata;

public class Type {
	private String id;
	private String name;

	public Type() {}

	public Type(String id, String name) {
		setId(id);
		setName(name);
	}

	@Override
	public String toString() {
		return "Type "+id+" "+name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Type other = (Type) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}

		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/* • Getters and setters • */
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}
