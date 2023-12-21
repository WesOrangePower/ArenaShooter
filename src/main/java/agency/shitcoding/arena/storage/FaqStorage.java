package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.Faq;

import java.util.Collection;

public interface FaqStorage {
    void store(Faq faq);

    Collection<Faq> getAll();
}
