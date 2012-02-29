(ns massive-chicken-attack.core
  (:require [cljminecraft.core :as c])
  (:import [org.bukkit Bukkit])
  (:import [org.bukkit.event Event Event$Type])
  (:import [org.bukkit.entity Animals Arrow Blaze Boat CaveSpider Chicken
            ComplexEntityPart ComplexLivingEntity Cow Creature Creeper Egg
            EnderCrystal EnderDragon EnderDragonPart Enderman EnderPearl
            EnderSignal ExperienceOrb Explosive FallingSand Fireball Fish
            Flying Ghast Giant HumanEntity Item LightningStrike LivingEntity
            MagmaCube Minecart Monster MushroomCow NPC Painting Pig PigZombie
            Player PoweredMinecart Projectile Sheep Silverfish Skeleton Slime
            SmallFireball Snowball Snowman Spider Squid StorageMinecart
            ThrownPotion TNTPrimed Vehicle Villager WaterMob Weather Wolf
            Zombie])
  (:import [org.bukkit.event.entity CreatureSpawnEvent CreeperPowerEvent
            EndermanPickupEvent EndermanPlaceEvent EntityChangeBlockEvent
            EntityCombustByBlockEvent EntityCombustByEntityEvent
            EntityCombustEvent EntityCreatePortalEvent EntityDamageByBlockEvent
            EntityDamageByEntityEvent EntityDamageByProjectileEvent
            EntityDamageEvent EntityDeathEvent EntityEvent EntityExplodeEvent
            EntityDamageEvent$DamageCause
            EntityInteractEvent EntityListener EntityPortalEnterEvent
            EntityRegainHealthEvent EntityShootBowEvent EntityTameEvent
            ;EntityTargetEvent EntityTeleportEvent ExplosionPrimeEvent
            EntityTargetEvent ExplosionPrimeEvent
            FoodLevelChangeEvent ItemDespawnEvent ItemSpawnEvent PigZapEvent
            PlayerDeathEvent PotionSplashEvent ProjectileHitEvent
            SheepDyeWoolEvent SheepRegrowWoolEvent SlimeSplitEvent]))

(def chicken-attacking (atom 0))
(defn player-attacks-chicken-event [_ player chicken]
  (when (not= 0 (rand-int 3))
    (let [location (.getLocation player)
          world (.getWorld location)]
      (swap! chicken-attacking inc)
      (future-call #(do
                      (Thread/sleep 20000)
                      (swap! chicken-attacking dec)))
      (doseq [x [-2 -1 0 1 2] z [-2 -1 0 1 2]]
        (let [chicken (.spawn world (.add (.clone location) x 3 z) Chicken)]
          (future-call #(do
                          (Thread/sleep 10000)
                          (.remove chicken))))))))

(defn get-entity-damage-listener []
  (c/auto-proxy
    [EntityListener] []
    (onEntityDamage [evt]
      (let [target (.getEntity evt)
            attacker (when (instance? EntityDamageByEntityEvent evt)
                       (.getDamager evt))]
        (when (and (instance? Player attacker) (instance? Chicken target))
          (player-attacks-chicken-event evt attacker target))))))

(defn chicken-touch-player [chicken player]
  (when (not= @chicken-attacking 0)
    (.teleport chicken (.getLocation player))
    (.damage player 3 chicken)))

(defn entity-touch-player-event []
  (doseq [player (Bukkit/getOnlinePlayers)]
    (let [entities (.getNearbyEntities player 2 2 2)
          chickens (filter #(instance? Chicken %) entities)]
      (doseq [chicken chickens]
        (chicken-touch-player chicken player)))))

(defn periodically []
  (entity-touch-player-event))

(def plugin-manager* (Bukkit/getPluginManager))
(def plugin* (.getPlugin plugin-manager* "massive-chicken-attack"))

(defn hehehe [f label]
  (let [listener (f)]
    (.registerEvent
      plugin-manager*
      (label c/event-types)
      listener
      (:Normal c/event-priorities)
      plugin*)))

(defn enable-plugin [plugin]
  (hehehe get-entity-damage-listener :ENTITY_DAMAGE)
  (.scheduleSyncRepeatingTask (Bukkit/getScheduler) plugin* (fn [] (periodically)) 50 50)
  (c/log-info "massive-chicken-attack started"))

(defn disable-plugin [plugin]
  (c/log-info "massive-chicken-attack stopped"))
