package uk.co.stikman.stikbot;

import java.util.List;

public class User {

	private final String	name;
	private List<String>	roles;
	private Nick	nick;

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getRoles() {
		return roles;
	}

	public boolean hasRole(String rolename) {
		if (rolename == null)
			return true;
		if (rolename.length() == 0)
			return true;
		for (String s : roles) 
			if (s.equalsIgnoreCase(rolename))
				return true;
		return false;
	}

	public void checkRole(String string) throws PermissionError {
		if (!hasRole(string))
			throw new PermissionError(string);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Nick getNick() {
		return nick;
	}
	
	public void setNick(Nick nick) {
		this.nick = nick;
	}
	
	
	
	@Override
	public String toString() {
		return this.name;
	}


	
	

}
