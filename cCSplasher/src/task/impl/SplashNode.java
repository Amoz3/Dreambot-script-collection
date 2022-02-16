package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import task.AbstractTask;

public class SplashNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isInitialised();
    }

    @Override
    public int execute() {
        if (!Magic.canCast(config.getBestSpell())) {
            config.setInitialised(false);
            return 1;
        }
        if (Magic.setAutocastSpell(config.getBestSpell())) {
            if (Combat.toggleAutoRetaliate(true)) {
                config.setStatus("Splashing!");

                if (SEAGULLS.contains(getLocalPlayer())) {


                    // splashing code
                    NPC seagull = NPCs.closest(s -> s != null && s.getName().equals("Seagull") && !s.isInCombat() && !s.isHealthBarVisible());
                    if (seagull != null) {
                        if (!getLocalPlayer().isInCombat()) {
                            seagull.interact("Attack");
                        } else {
                            if (Calculations.random(1, 20) == 3) {
                                // 1/20 chance ever 30-38.2 seconds player will reattack seagull, to avoid the 20 minute stop attacking thing
                                log("clicking...");
                                getLocalPlayer().getCharacterInteractingWithMe().interact("Attack");
                                sleepUntil(() -> !getLocalPlayer().isInCombat(), Calculations.random(30000, 38200));
                            }
                        }
                    }
                } else {
                    // running code
                    config.setStatus("going to seagulls");

                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(18, 32)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(SEAGULLS.getRandomTile());
                    }
                }
            }
        }
            sleep(1000);
            return 0;
    }
}
