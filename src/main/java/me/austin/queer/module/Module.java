package me.austin.queer.module;

/**
 * default implementation of IModule
 */
public abstract class Module implements IModule {
	private final String name;
	private final String description;
	
	public Module(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
}
