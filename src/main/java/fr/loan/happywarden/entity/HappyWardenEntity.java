package fr.loan.happywarden.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HappyWardenEntity extends CreatureEntity {

    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(HappyWardenEntity.class, DataSerializers.BOOLEAN);

    public HappyWardenEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    // Attributs de base (vie, vitesse, dégâts)
    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MobEntity.func_233666_p_()
            .createMutableAttribute(Attributes.MAX_HEALTH, 500.0D)
            .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
            .createMutableAttribute(Attributes.ATTACK_DAMAGE, 20.0D);
    }

    // Données réseau & NBT pour la selle
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SADDLED, false);
    }

    public boolean isSaddled() {
        return this.dataManager.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.dataManager.set(SADDLED, saddled);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Saddle", this.isSaddled());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSaddled(compound.getBoolean("Saddle"));
    }

    // IA
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, true));
    }

    // Interaction avec le joueur
    @Override
    protected ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Mettre la selle
        if (stack.getItem() == Items.SADDLE && !this.isSaddled()) {
            this.setSaddled(true);
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 1.0F, 1.0F);

            if (!player.abilities.isCreativeMode) {
                stack.shrink(1); // Consomme la selle en mode survie
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }

        // Monter sur le mob uniquement s'il a une selle et si le joueur ne s'accroupit pas
        if (this.isSaddled() && !this.isBeingRidden() && !player.isSecondaryUseActive()) {
            if (!this.world.isRemote) {
                player.startRiding(this);
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }

        return super.getEntityInteractionResult(player, hand);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        // Le joueur contrôle le mob uniquement si ce dernier porte une selle
        return (this.isSaddled() && !this.getPassengers().isEmpty()) ? this.getPassengers().get(0) : null;
    }

    @Override
    public double getMountedYOffset() {
        return 3.0D;
    }

    public void jumpFromRider(PlayerEntity rider) {
        if (rider == this.getControllingPassenger() && this.onGround) {
            this.jump();
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isAlive()) {
            Entity passenger = this.getControllingPassenger();

            if (this.isBeingRidden() && passenger instanceof LivingEntity) {
                LivingEntity rider = (LivingEntity) passenger;

                // Aligne la rotation du mob sur celle du joueur
                this.rotationYaw = rider.rotationYaw;
                this.prevRotationYaw = this.rotationYaw;
                this.rotationPitch = rider.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.setRotationYawHead(this.rotationYaw);
                this.setRenderYawOffset(this.rotationYaw);

                // Récupère les entrées clavier du joueur
                float strafe = rider.moveStrafing * 0.5F;
                float forward = rider.moveForward;

                if (forward <= 0.0F) {
                    forward *= 0.25F; // Recule plus lentement
                }

                // Applique la vitesse de déplacement
                this.setAIMoveSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vector3d(strafe, travelVector.y, forward));

                return;
            }
        }
        super.travel(travelVector);
    }
}