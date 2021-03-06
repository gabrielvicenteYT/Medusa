package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.EvictingList;

@CheckInfo(name = "AutoClicker (B)", description = "Checks for consistency in clicks.")
public class AutoClickerB extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(120);

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            final long delay = data.getClickProcessor().getDelay();

            if (delay > 5000L) {
                samples.clear();
                return;
            }

            samples.add(delay);

            if (samples.isFull()) {
                final double deviation = MathUtil.getStandardDeviation(samples);

                if (deviation < 150) {
                    if (increaseBuffer() > 100) {
                        fail("deviation=" + deviation + " buffer=" + getBuffer());
                        multiplyBuffer(0.75);
                    }
                } else {
                    decreaseBufferBy(24);
                }
            }
        }
    }
}
