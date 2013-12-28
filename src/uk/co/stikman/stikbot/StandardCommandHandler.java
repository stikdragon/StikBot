package uk.co.stikman.stikbot;

public abstract class StandardCommandHandler implements CommandHandler {

	private String		desc;
	private String		name;
	private BaseModule	module;
	private String		requiredRole;

	public StandardCommandHandler(BaseModule module, String name, String desc) {
		this(module, name);
		this.desc = desc;
	}

	public StandardCommandHandler(BaseModule module, String name) {
		this.name = name;
		this.module = module;
	}

	public StandardCommandHandler(CoreModule module, String name, String requiredRole, String desc) {
		this(module, name, desc);
		this.requiredRole = requiredRole;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (module.isRoot())
			return name;
		return module.getName() + "." + name;
	}

	@Override
	public boolean canHandle(String command) {
		if (command == null)
			return false;
		if (command.startsWith(module.getName() + "."))
			return command.equalsIgnoreCase(module.getName() + "." + name);
		else
			return (command != null && command.equals(name));
	}

	@Override
	public BaseModule getModule() {
		return module;
	}

	@Override
	public String getRequiredRole() {
		return requiredRole;
	}

}
