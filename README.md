# ![logo_meteor](https://github.com/user-attachments/assets/63402bad-1439-4fb9-8aab-a386f0ce75d8)

Add _spice_ to your world, with giant peppers of molten (or frozen!) rock that fall from the sky!

This mod adds meteors, chunks of rock that randomly fall from the sky every now and then, exploding on impact and leaving useful resources in the impact craters they make. The explosions are even better than vanilla's ones!

You will be able to craft lasers to defend yourself from the destructive power of these touchy sky rocks, or maybe you could find some cuteness inside some them! Meow!

![oh my meteor](https://github.com/user-attachments/assets/2b4d86fe-4e99-4844-82ad-42cd914a147b)

Video showcase of version 0.1.0 of the mod by Boodlyneck:
<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/LWCvP63pooQ" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

This mod is extremely configurable, meaning that you can adjust it however you like. Would you like more metors at night? Cool!
Would like them not to explode anything? Done! And a lot of other settings!

Inspired by the old meteor mods and commissioned by SlayerTheChikken

Also checkout the Tekxit modpack: [![Tekxit](https://cdn.technicpack.net/platform2/pack-icons/1253751.png?1748788053)](https://tekxit.lol/)

A fan of cool mods? Take a look at what else [i've developed](https://modrinth.com/user/emafire003), might find something intresting like [LightWithn](https://modrinth.com/mod/lightwithin) which adds powers unique to each and every player!

## Feature list
### Meteor sizes
Meteors can spawn in a variety of different sizes, the bigger the size the bigger the damage and the "structure" that will be left behind. You can adjust which size of meteors is able to spawn and how frequently the meteors spawn. 
#### Customizable meteors
You can add your own meteor structures using a datapack, like the example one! You can specify in which size category your structure can be spawned, and if it's special or not. (special ones have a lower chance of appearing). They are NBT structure files, like the ones from the Structure Block. See [example datapack](https://github.com/TeamChikken/OhMyMeteors/releases)
### Lasers blocks
They are used to defend your base from the falling meteorites, by shooting a laser at the meteor and destroying it while midair

There are two tiers of lasers: 
- The basic one has a shorter, but still very considerable range by default (you can change it in the config file!). It also has a loger cooldown time (3s by default, you can even disable it all together) and won't be able to destroy meteors past a certain size (up to sizes that are 1/3 smaller than the biggest, configurable too)
- The advanced laser, bigger area coverage, shorter cooldown time and can destroy every type of laser. You will however need to gather some meteorich chunks to craft meteoric alloy in order to power up your basic laser to this advanced version

You can interact with the lasers using a **Focusing Lenses** item to make them display the area that they are currently covering. When a meteor hits this area the laser activates and explodes the meteor.
### Items
This mod also adds a few items, **Meteoric Chunks** which can be obtained when breaking (without Silk Touch) the Meteoric Rock that spawns when meteors explode on land. This can be tuned into 9 raw iron or used to craft **Meteoric Alloy**. You will need to combine 2 Meteoric Chunks with 2 Iron Ingots to obtain 2 of the alloy, which you can then use in the crafting for the Advanced Laser.
Then we have the **Focusing Lenses**, which are used in the crafting of the two lasers. They require lime glass, amethyst, and quartz.

### Meteor structure - Customizable meteors!
The ones that spawn when a meteor explodes. They are located in the folder /data/ohmymeteors/structure(*s* if below mc1.21) and you can customize them using a datapack. They are the same files that you get out of the Structure Block in vanilla. From version 1.2.0 there is an easier way to add custom meteors with a command, see more below in the command section!

![cool-ezgif com-optimize(1)](https://github.com/user-attachments/assets/75bb0a16-ded6-465c-82d6-ee43506ce3f0)

![Meteorimpact-ezgif com-optimize](https://github.com/user-attachments/assets/e65a71bf-1d3a-40b6-b039-9f4a46ab1238)

And an example datapack is located [here](https://github.com/TeamChikken/OhMyMeteors/releases). If you want to remove default structures from spawning, you can add a structure file (copy one of the default ones for instance) and name it `ignore_<nameofthestructuretoremvoe>.nbt`, or if you want to ignore all of the use `ignoredefault.nbt`.

These structures can also contain entities, like the:

### Meteor cat
A cute kitty could be travelling the galaxy at any given moment. And it could land on your minecraft world (as long as you don't shoot it down with a laser that is).
They can survive in the fire, have a 15HP instead of 10HP, and if you so chose to dispose of them, they will drop some meteoric chunks (reminding you of your cruelty and cosmic-feline-bloodstained hands)

### Meteor showers! 
Meteor showers are a rare occurrence where a cluster of meteors fall together at the same time. Three types of meteor showers have been added:
- 'instant' where all the meteors fall at the same time around a central point 
- 'delayed' with each meteor spawning a bit after the next one
- 'delayed directioned' with each meteor spawning after the next one but with all meteors following the same general trajectory

//TODO make a video showcase and link it here

### Config file
Located at `/config/ohmymeteros/ohmymeteors_config.yml` it contains a lot of settings that you can adjust to your liking. Remember that if you play on a server, the server's settings are the one that are going to be used (this is also part of the reason why there is no GUI).

You can reload the config in game using `/omm reload`.

If somehow the config gets corrupted or updates in a weird way, a copy of the corrupted file will be created to allow you to back it up, and a new resetted one will be generated and used as well.

<details>
  <summary>Config file version 1.2.0</summary>
  
  ```yaml
#The version of the config. DO NOT CHANGE IT :D | default= 5 | type= Integer
version:5

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
#A factor to ADD to the explosion power (by default, the power is equal to the meteor size), thus increasing the damage and radius of the explosion. Also supports negative numbers | default= 0 | type= Integer
explosion_power_modifier:0
#A factor to ADD to the speed at which the meteor falls downwards. It is added to a randomly generated number between 1 and 0. Also supports negative numbers | default= 0 | type= Integer
downwards_speed_modifier:0

#This option has no effect as of version 0.4.0. Please use the 'ohmymeteors:meteor_bypasses' tag instead. | default= true | type= Boolean
should_bypass_leaves:true
#This option has no effect as of version 0.4.0. Please use the 'ohmymeteors:meteor_bypasses_and_destroy' tag instead | default= true | type= Boolean
should_destroy_leaves:true
#Should meteors be (more or less) directed towards the nearest player? | default= false | type= Boolean
homing_meteors:false
#Should players get a message in chat/hotbar when a meteor spawns? | default= false | type= Boolean
announce_meteor_spawn:false
#Should players get a message in chat/hotbar when a meteor is destroyed? | default= false | type= Boolean
announce_meteor_destroyed:false
#Should the above announcement be displayed above the hotbar in the actionbar or in chat? | default= true | type= Boolean
actionbar_announcements:true
#If announcements are enabled, should they also display the (approximate) coordinates of the meteor being spawned/destroyed? | default= true | type= Boolean
announce_location:true
#Should the explosion sound of the meteor be heard by all players online? | default= false | type= Boolean
global_explosion_sound:false
#Should the explosion sound of the meteor be heard by all players around a certain area from the impact point? | default= false | type= Boolean
area_explosion_sound:false
#The radius in blocks of the area in which the sound of the meteor will be heard if the option above is true | default= 500 | type= Integer
area_explosion_sound_radius:500

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
#Should the meteor structure only replace air blocks? | default= false | type= Boolean
only_replace_air:false
#Should the meteors that come out of a bigger meteor when it's broken only replace air blocks for their structure? | default= true | type= Boolean
scatter_only_replace_air:true

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
#The maximum size of meteor that can be considered big, and will spawn a big meteor structure upon impact. Only these can spawn a meteor cat by default. | default= 20 | type= Integer
max_big_meteor_size:20
#The chance of spawning a special meteor structure in a certain size category (for example the meteor cat meteor) (works like the other chances, aka 1 in x probability) | default= 10 | type= Integer
special_meteors_chance:10

#Should meteor and laser particles be forced? They will be rendered further away and look better, but if there are too many of them you may want to disable this for lag reasons. Note: some particles will never displays as forced, like the lasers target box | default= true | type= Boolean
use_forced_particles:true

#A list of the IDs of the dimensions in which meteors can naturally spawn in, vanilla or not. | default= [minecraft:overworld, minecraft:the_end] | type= List12
spawn_dimensions:[minecraft:overworld, minecraft:the_end]
#A map consisting of dimension=chance of spawning. The dimension must be present in the list above, otherwise meteors won't spawn at all. This chance will ALWAYS ovveride the default if present. The spawn chance works as described above. | default= {minecraft:the_end=200000} | type= Map1
dimension_chances:{minecraft:the_end=200000}
#The same as above but with a possibly different chance at night if enabled | default= {minecraft:the_end=100000} | type= Map1
dimension_night_chances:{minecraft:the_end=100000}
#If set to false will behave like a blacklist, aka meteors won't spawn in those biomes. If true will behave like a whitelist, meteors will spawn ONLY in those biomes. | default= false | type= Boolean
biome_list_mode:false
#A black or whitelist of the biomes in which meteor will or will not spawn according to the setting above | default= [minecraft:cherry_grove, minecraft:soul_sand_valley, modname:modbiome] | type= ListN
biome_spawn_list:[minecraft:cherry_grove, minecraft:soul_sand_valley, modname:modbiome]
#A map consisting of biome=chance of spawning. The spawn chance works as described above. The meteors must be able to spawn in those biomes, otherwise they won't! | default= {modname:modbiome=2025, minecraft:desert=19990} | type= MapN
biome_chances:{modname:modbiome=2025, minecraft:desert=19990}
#The same as above but with a possibly different chance at night if enabled | default= {modname:modbiome=2025, minecraft:desert=9995} | type= MapN
biome_night_chances:{modname:modbiome=2025, minecraft:desert=9995}

#If true will use a spherical explosion instead of the vanilla cubical one. These look nicer at higher explosion power/ranges, but after power 100 become quite laggy. | default= true | type= Boolean
use_better_explosions:true
#How far should meteors be rendered. WARNING: YOU NEED TO RESTART YOUR SERVER AND CLIENT IF YOU CHANGE THIS VALUE in order for it to take effect. It is multiplied by the 'entity render' distance of the server | default= 200 | type= Integer
meteor_render_distance:200

#If true, there will be a chance that meteor showers will spawn (a lot of metors spawning at the same time & place) | default= true | type= Boolean
meteor_showers_enabled:true
#The chance for a meteor shower to spawn (on top of the normal meteor spawning chance, so it's meteor_spawn*meteor_shower spawn) | default= 100 | type= Integer
meteor_shower_chance:100
#The minimum number of meteors that are going to spawn in the meteor shower. | default= 5 | type= Integer
min_meteors_in_shower:5
#The maximum number of meteors that are going to spawn in the meteor shower. | default= 20 | type= Integer
max_meteors_in_shower:20

  ```
  
</details>


## Commands
They are are a few commands:
- ```/omm spawn <size> [velocity]```
Will spawn a meteor in your looking direction with the specified size and the optionally specified velocity
- ```/omm spawn random```
Will spawn a random meteor in a random direction in a random postiont around you
- ```/omm spawn natural```
Will spawn a meteor as if it had spawned naturally
- ```/omm spawn shower [instant|delayed|delayed_directioned]```
Will spawn a meteor shower as if it had spawned naturally, with the specified type of shower.
- ```/omm config reload```
Will reload the config file
- ```/omm config open```
Will open the config file if in singleplayer

### Adding custom meteor structures/schematics
```/omm custom```
And its sub commands will allow you to generate a datapack and add your meteor structures directly in game without needing to touch any files. You can then open the datapack folder and send the datapack to your friends if you want to!

#### Adding a new meteor
```/omm custom add <type_of_schematic> <structureId/schemId> <size> <special>```

`type_of_schematic`: is the type of schematic/structure file that is going to be used to spawn the meteor's structure. Three types are currently supported thanks to SchemConvert, and they are:
- `structureblock`: the vanilla `.nbt` structure files that you can create using a structureblock
- `worldedit_schematic`: worldedit's `.schem` files which you can create with the `//schem` command
- `litematica_schematic`: litematica's `.liteschem` files
Especially for the schematic files it's important to not move them from where they are generated

`structureId/schemId`: is the name of the structure/schematic file that you want to use. You don't need to specify the extension (like `.schem` or `.nbt`). This name is the same one that will be used by the meteor strucuture id, but you can change it later with the edit command if needed

`size`: the size correspondig to this new meteor structure, can be `SMALL`,`MEDIUM`,`BIG`,`HUGE`

`special`: weather or not this is a special meteor aka if this structure has a lower than normal chance of appearing

#### Removing default meteors
`/omm custom ignoredefaults [true|false]`
If you set _ignoredefaults_ to true this mod's default meteors will be disabled and only custom ones will be used. Make sure to add at least one meteor structure per category otherwise it will give an error.

#### Removing meteor structures
`/omm custom remove <structureId> <size> <special>`
If you want to remove a meteor that you have previously added you can use this command. You will need to specifiy the structure's name, the corresponding size and if it is a special meteor

#### Editing meteor structure
`/omm custom edit <originalName> <ogSize> <ogSpecial> <newName> <newSize> <newSpecial>`
You can use this command to edit a structure file name, class and if it's a special one or not. Pretty intuitive by now i think.

#### Seeing which meteors you have added so far
`/omm custom display_current`
Will display all the meteors corresponding to a certain size and special. It will also display if you have currently disabled default meteors or not


### Mod compatibility
You can use Flan and YetAnotherWorldProtector to define regions where meteors won't spawn or explode.
Now you will be able to use either Flan or YAWP to define regions where meteors can't spawn or explode. Flan has its own global setting, while to disable the meteor spawning in YAWP you will need to set the flag explosions-entities to denied.

### License
MIT

### Credits
SlayerTheChikken for some assets and the idea of the mod
Explodee mod (by KnownSH) for the code of the spherical explosions https://github.com/KnownSH/Explodee
SchemConvert to allow the conversion between schematic formats https://github.com/PiTheGuy/SchemConvert
