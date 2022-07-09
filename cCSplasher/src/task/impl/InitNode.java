package task.impl;

import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 28 aug 2021
 * desc: once logged in check for best spell & magic bonus
 */
public class InitNode extends AbstractTask {
    int highestHit = 0;

    @Override
    public boolean accept() {
        return !config.isInitialised();
    }

    @Override
    public int execute() {
        config.setStatus("Looking for spell to cast");

        config.setBestSpell(null); // set it to null when this runs after splashnode it has the chance to end
        highestHit = 0; // same here

        for (Normal spell : Normal.values()) {
            // filter out iban blast, crumble undead and other fucky spells
                if (spell.toString().contains("Wind")
                        || spell.toString().contains("Water")
                        || spell.toString().contains("Earth")
                        || spell.toString().contains("Fire")) {
                    if (Magic.canCast(spell)) {
                        if (spell.getMaxHit() > highestHit) {
                            config.setBestSpell(spell);
                            highestHit = spell.getMaxHit();
                        }
                    }
                }
            }

        if (config.getBestSpell() == null) {
            log("seems like you are out of runes, if you arent please contact camalCase on dreambot forums");
            manager.stop();
        }


        log("Strongest spell: " + config.getBestSpell());
        if (config.getBestSpell() != null) {
            config.setInitialised(true);
        }
        return 100;
    }
}
