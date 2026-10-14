package fr.loan.happywarden.entity;

import java.util.List;

import javax.annotation.Nullable;

import fr.loan.happywarden.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class HappyWardenEntity extends TameableEntity {

    private static final double BEAM_RANGE = 32.0D;
    private static final float BEAM_DAMAGE = 100.0F;
    private int beamCooldown;

    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(HappyWardenEntity.class, DataSerializers.BOOLEAN);

    public HappyWardenEntity(EntityType<? extends HappyWardenEntity> type, World world) {
        super(type, world);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
        return null;
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
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
            @Override
            public boolean shouldExecute() {
                return !HappyWardenEntity.this.isTamed() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, true));
    }

    // Interaction avec le joueur
    @Override
    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!this.isTamed() && stack.getItem() == ModItems.WARDEN_BREAD.get()) {
            if (!this.world.isRemote) {
                this.setTamedBy(player);
                this.setAttackTarget(null);
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }

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
    public void tick() {
        super.tick();
        if (this.beamCooldown > 0) {
            this.beamCooldown--;
        }
    }

    public void fireLightBeam(PlayerEntity rider) {
        if (this.world.isRemote || rider != this.getControllingPassenger() || this.beamCooldown > 0) {
            return;
        }

        this.beamCooldown = 80;
        ServerWorld serverWorld = (ServerWorld) this.world;
        Vector3d start = this.getPositionVec().add(0.0D, this.getHeight() * 0.58D, 0.0D);
        Vector3d direction = rider.getLookVec().normalize();
        Vector3d actualEnd = start.add(direction.scale(BEAM_RANGE));
        BlockPos previousBlock = null;

        for (double distance = 0.0D; distance <= BEAM_RANGE; distance += 0.35D) {
            Vector3d point = start.add(direction.scale(distance));
            BlockPos blockPos = new BlockPos(point);

            if (!blockPos.equals(previousBlock)) {
                previousBlock = blockPos;
                BlockState blockState = this.world.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    actualEnd = point;
                    this.world.createExplosion(this, point.x, point.y, point.z, 4.0F, Explosion.Mode.DESTROY);
                    serverWorld.spawnParticle(ParticleTypes.EXPLOSION_EMITTER,
                            point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    break;
                }
            }

            serverWorld.spawnParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            actualEnd = point;
        }

        AxisAlignedBB beamBounds = new AxisAlignedBB(start, actualEnd).grow(1.0D);
        List<LivingEntity> targets = this.world.getEntitiesWithinAABB(LivingEntity.class, beamBounds,
                target -> target != this && target.isAlive());
        for (LivingEntity target : targets) {
            if (target != rider && !this.isOnSameTeam(target) && this.canAttack(target)
                    && target.getBoundingBox().grow(0.2D).intersects(start, actualEnd)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), BEAM_DAMAGE);
            }
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