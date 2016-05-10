package models;

import java.util.List;

public class EntityList<E> {

	private List<E> entityList;
	
	private int count;
	
	public EntityList( List<E> entityList, int count ){
		this.entityList = entityList;
		this.count = count;
	}

	public List<E> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<E> entityList) {
		this.entityList = entityList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
