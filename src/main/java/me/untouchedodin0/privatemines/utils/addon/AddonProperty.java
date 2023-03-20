package me.untouchedodin0.privatemines.utils.addon;

public class AddonProperty {

  private String main;
  private String name;
  private String version;
  private String description;

  public AddonProperty(String main, String name, String version, String description) {
    this.main = main;
    this.name = name;
    this.version = version;
    this.description = description;
  }

  public String getMain() {
    return main;
  }

  public void setMain(String main) {
    this.main = main;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
