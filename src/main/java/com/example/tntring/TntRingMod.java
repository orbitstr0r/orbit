package com.example.tntring;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("tntringmod")
public class TntRingMod {

    public TntRingMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!event.getItemStack().is(Items.FISHING_ROD)) return;

        FishingHook bobber = player.fishing;
        if (bobber != null && bobber.isAlive()) {
            ServerLevel level = (ServerLevel) player.level();
            Vec3 center = bobber.position();

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 4, false, false));

            level.playSound(null, center.x, center.y, center.z,
                    SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 2.0F, 0.8F);

            spawnTntRing(level, player, center, 12, 0.75, 15, 6.0F);
            spawnTntRing(level, player, center, 24, 1.35, 25, 6.0F);
            spawnTntRing(level, player, center, 36, 2.05, 35, 6.0F);
        }
    }

    private void spawnTntRing(ServerLevel level, Player owner, Vec3 center, int count, double speed, int fuseTicks, float power) {
        for (int i = 0; i < count; i++) {
            double angle = (2.0 * Math.PI * i) / count;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);

            PrimedTnt tnt = new PrimedTnt(level, center.x + dx * 0.8, center.y + 0.5, center.z + dz * 0.8, owner);
            tnt.setFuse(fuseTicks);
            tnt.setDeltaMovement(dx * speed, 0.38, dz * speed);

            CompoundTag tag = tnt.saveWithoutId(new CompoundTag());
            tag.putFloat("explosion_power", power);
            tnt.load(tag);

            level.addFreshEntity(tnt);
        }
    }
}
