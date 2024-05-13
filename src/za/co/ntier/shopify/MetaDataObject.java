package za.co.ntier.shopify;

public class MetaDataObject {
	
	String id;
	String key;
	String value;
	public MetaDataObject(String id, String key, String value) {
		
		this.id = (id != null) ? id : "";
		this.key = (key != null) ? key : "";
		this.value = (value != null) ? value : "";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	

}
