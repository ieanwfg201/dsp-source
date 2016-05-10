package models;

public class User {
	
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}


	private String guid;
	
	private String name;
	
	private String address;
	
	private String city;
	
	private String country;
	
	private String userName;
	
	private String password;
	
	private String email;
	
	
	public String validate() {
//        if (authenticate(email, password) == null) {
//            return "Invalid email or password";
//        }
        return null;
    }


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
