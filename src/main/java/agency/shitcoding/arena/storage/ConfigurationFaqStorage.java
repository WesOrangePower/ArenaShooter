package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import agency.shitcoding.arena.models.Faq;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ConfigurationFaqStorage implements FaqStorage {

  private static final Logger LOG = ArenaShooter.getInstance().getLogger();
  public static final String FILE_NAME = "faq.yml";

  private final Configuration configuration;


  @Override
  public void store(Faq faq) {
    ConfigurationSection allFaqsSection = configuration.getConfigurationSection(Conf.faqSection);
    if (allFaqsSection == null) {
      allFaqsSection = configuration.createSection(Conf.faqSection);
    }
    ConfigurationSection faqSection = allFaqsSection.createSection(UUID.randomUUID().toString());

    faqSection.set(Conf.Faqs.title, faq.getTitle());
    faqSection.set(Conf.Faqs.content, faq.getContent());
    save();
  }

  @Override
  public Collection<Faq> getAll() {
    ConfigurationSection allFaqsSection = configuration.getConfigurationSection(Conf.faqSection);
    if (allFaqsSection == null) {
      allFaqsSection = configuration.createSection(Conf.faqSection);
    }
    Set<String> keys = allFaqsSection.getKeys(false);
    List<Faq> faqs = new ArrayList<>();
    for (String key : keys) {
      ConfigurationSection faqSection = allFaqsSection.getConfigurationSection(key);
      Faq faq = parseFaq(faqSection);
      if (faq == null) {
        LOG.warning(() -> "Failed to read FAQ: " + key);
        continue;
      }
      faqs.add(faq);
    }
    return faqs;
  }

  @Contract("null -> null")
  private Faq parseFaq(@Nullable ConfigurationSection section) {
    if (section == null ) {
      return null;
    }
    String title = section.getString(Conf.Faqs.title);
    String content = section.getString(Conf.Faqs.content);
    if (title == null) {
      LOG.warning("No title for FAQ: " + section.getCurrentPath());
      return null;
    }
    if (content == null) {
      LOG.warning(() -> "No content for FAQ: " + title);
      return null;
    }
    return new Faq(title, content);
  }

  private void save() {
    if (configuration instanceof YamlConfiguration yamlConfiguration) {
      try {
        yamlConfiguration.save(FILE_NAME);
      } catch (Exception e) {
        ArenaShooter.getInstance().getLogger().severe("Failed to save " + FILE_NAME);
      }
    }
  }
}
