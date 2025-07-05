# ![logo_meteor](https://github.com/user-attachments/assets/63402bad-1439-4fb9-8aab-a386f0ce75d8)

Add _spice_ to your world, with giant peppers of molten (or frozen!) rock that fall from the sky!

This mod adds meteors, chunks of rock that randomly fall from the sky every now and then, exploding on impact and leaving useful resources in the impact crates they make.

You will be able to craft lasers to defend yourself from the destructive power of these touchy sky rocks, or maybe you could
find some cuteness inside some them! Meow!

This mod is extremely configurable, meaning that you can adjust it however you like. Would you like more metors at night? Cool!
Would like them not to explode anything? Done! And a lot of other settings!

Inspired by the old meteor mods and commissioned by SlayerTheChikken

## Feature list
### Meteor sizes
Meteors can spawn in a variety of different sizes, the bigger the size the bigger the damage and the "structure" that will be left behind. You can adjust which size of meteors is able to spawn and how frequently the meteors spawn. 
### Lasers
There are two tiers of lasers: 
- The basic one has a shorter, but still very considerable range by default (you can change it in the config file!). It also has a loger cooldown time (3s by default, you can even disable it all together) and won't be able to destroy meteors past a certain size (up to sizes that are 1/3 smaller than the biggest, configurable too)
- The advanced laser, bigger area coverage, shorter cooldown time and can destroy every type of laser. You will however need to gather some meteorich chunks to craft meteoric alloy in order to power up your basic laser to this advanced version

You can interact with the lasers using a **Focusing Lenses** item to make them display the area that they are currently covering. When a meteor hits this area the laser activates and explodes the meteor.
### Items
This mod also adds a few items, **Meteoric Chunks** which can be obtained when breaking (without Silk Touch) the Meteoric Rock that spawns when meteors explode on land. This can be tuned into 9 raw iron or used to craft **Meteoric Alloy**. You will need to combine 2 Meteoric Chunks with 2 Iron Ingots to obtain 2 of the alloy, which you can then use in the crafting for the Advanced Laser.
Then we have the **Focusing Lenses**, which are used in the crafting of the two lasers. They require lime glass, amethyst, and quartz.

### Meteor structures
The ones that spawn when a meteor explodes. They are located in the folder /data/ohmymeteors/structure(*s* if below mc1.21) and you can customized them using a datapack. They are the same files that you get out of the Structure Block in vanilla. 

### Meteor cat
A cute kitty could be travelling the galaxy at any given moment. And it could land on your minecraft world (as long as you don't shoot it down with a laser that is).
They can survive in the fire, have a 15HP instead of 10HP, and if you so chose to dispose of them, they will drop some meteoric chunks (reminding you of your cruelty and cosmic-feline-bloodstained hands)

### Config file
Located at `/config/ohmymeteros/ohmymeteors_config.yml` it contains a lot of settings that you can adjust to your liking. Remember that if you play on a server, the server's settings are the one that are going to be used (this is also part of the reason why there is no GUI).

You can reload the config in game using `/omm reload`.

If somehow the config gets corrupted or updates in a weird way, a copy of the corrupted file will be created to allow you to back it up, and a new resetted one will be generated and used as well.

<details>
  <summary>Config file as of first release</summary>
  
  ```yaml
#The version of the config. DO NOT CHANGE IT :D | default= 1 | type= Integer
version:1

#Expressed in blocks, represents the min distance (as in a radius) from the origin of the meteor (like a player) in which the meteor wont' spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless) | default= 2 | type= Integer
min_meteor_spawn_distance:2
#Expressed in blocks, represents the max distance (as in a radius) from the origin of the meteor (like a player) in which a meteor can spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless) | default= 25 | type= Integer
max_meteor_spawn_distance:25
#The world height (y level) at which meteors spawn in | default= 300 | type= Integer
meteor_spawn_height:300
#Expressed as '1 in <x>' chances of spawning a meteor. For example, by default it has a chance of 1 in 20000 | default= 20000 | type= Integer
meteor_spawn_chance:20000
#Should the spawn rate be different during the night? | default= false | type= Boolean
modify_spawn_chance_at_night:false
#Should huge meteors be able to spawn? They are meteors bigger than the maximum size of the big ones | default= true | type= Boolean
spawn_huge_meteors:true
#The chance for a spawned meteor to be of huge size. Expressed as in 1 in x chances. (on top of the 'normal' spawning chance) | default= 100 | type= Integer
huge_meteor_chance:100
#The size limit of how big a huge meteor can be | default= 35 | type= Integer
huge_meteor_size_limit:35
#Should the spawn rate be different during the night? | default= false | type= Boolean
modify_spawn_chance_at_night:false
#The chance for a meteor to spawn at night if enabled. Expressed as in 1 in x chances. | default= 10000 | type= Integer
meteor_night_spawn_chance:10000
#The smallest size a natural meteor can have when spawned in. Cannot go below 1 | default= 1 | type= Integer
natural_meteor_min_size:1
#The biggest size a natural meteor can have when spawned in. Cannot go above 50. | default= 10 | type= Integer
natural_meteor_max_size:10

#Should meteors bypass leaves blocks instead of colliding with them midair? | default= true | type= Boolean
should_bypass_leaves:true
#Should meteors be (more or less) directed towards the nearest player? | default= false | type= Boolean
homing_meteors:false
#Should players get a message in chat/hotbar when a meteor spawns? | default= false | type= Boolean
announce_meteor_spawn:false
#Should players get a message in chat/hotbar when a meteor is destroyed? | default= false | type= Boolean
announce_meteor_destroyed:false
#Should the above announcement be displayed above the hotbar in the actionbar or in chat? | default= true | type= Boolean
actionbar_announcements:true

#Should there be a cooldown between a meteor spawning one meteor and then another? | default= true | type= Boolean
should_cooldown_between_meteors:true
#The minimum time interval (in seconds) between spawning a meteor and then another | default= 20 | type= Integer
min_meteor_cooldown_time:20
#Should meteors be able to destroy blocks on impact? | default= true | type= Boolean
meteor_griefing:true
#Should the meteors that come out of a bigger meteor when it's broken be able to destroy blocks on impact? | default= true | type= Boolean
scatter_meteor_griefing:true
#Should meteors spawn the meteor structure after impact? | default= true | type= Boolean
meteor_structure:true
#Should the meteors that come out of a bigger meteor when it's broken be able to destroy spawn structures on impact? | default= true | type= Boolean
scatter_meteor_structure:true

#The radius in blocks of the xz area covered by the Basic laser block, where meteors will be blown up | default= 32 | type= Integer
basic_laser_area_radius:32
#How many blocks up from the position of the basic laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way) | default= 64 | type= Integer
basic_laser_height:64
#The radius in blocks of the xz area covered by the advanced laser block, where meteors will be blown up | default= 48 | type= Integer
advanced_laser_area_radius:48
#How many blocks up from the position of the advanced laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way) | default= 64 | type= Integer
advanced_laser_height:64
#Should the laser be in a cooldown where it can't fire, after it has just fired? | default= true | type= Boolean
should_basic_laser_cooldown:true
#How many seconds should this cooldown last? | default= 3 | type= Integer
basic_laser_cooldown:3
#Should the laser be in a cooldown where it can't fire, after it has just fired? | default= true | type= Boolean
should_advanced_laser_cooldown:true
#How many seconds should this cooldown last? | default= 1 | type= Integer
advanced_laser_cooldown:1

#The maximum size of meteor that can be considered small, and will spawn a small meteor structure upon impact | default= 4 | type= Integer
max_small_meteor_size:4
#The maximum size of meteor that can be considered medium, and will spawn a medium meteor structure upon impact | default= 7 | type= Integer
max_medium_meteor_size:7
#The maximum size of meteor that can be considered big, and will spawn a big meteor structure upon impact. Only these can spawn a meteor cat by default. | default= 7 | type= Integer
max_big_meteor_size:7
  ```
  
</details>


#### Commands
They are meant more for debugging purposes, but if need there are a few commands:
```/omm spawn <size> [velocity]```
Will spawn a meteor in your looking direction with the specified size and the optionally specified velocity
```/omm spawn random```
Will spawn a random meteor in a random direction in a random postiont around you
```/omm reload```
Will reload the config file

## License
MIT
