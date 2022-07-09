package task.impl;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import task.AbstractTask;

public class SplashNode extends AbstractTask {
    private Timer afkTimer = new Timer();

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
                        if (!getLocalPlayer().isInCombat() || afkTimer.elapsed() > (1000 * 60 * 18)) { // 18 min
                            seagull.interact("Attack");
                            afkTimer.reset();
                        } else {
                            if (Mouse.isMouseInScreen()) {
                                Mouse.moveMouseOutsideScreen();
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
            return 1000;
    }
}
