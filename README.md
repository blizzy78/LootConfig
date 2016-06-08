# LootConfigDump

This is a tool that dumps statistics about the loot configuration on a Rust server.

Sample output:

```text
assets/bundled/prefabs/autospawn/resource/loot/loot-barrel-1.prefab (blueprint fragments: yes)
  37.532% Nothing
  4.878% Bow And Arrow (arrow.wooden x5, bow.hunting)
  4.065% HQMetal (metal.refined x5)
  3.265% bucket.water (bucket.water)
  3.265% stonehatchet (stonehatchet)
  3.265% Torch (torch)
  2.439% Crossbow (arrow.wooden x5, crossbow)
  41.291% other (328)
assets/bundled/prefabs/autospawn/resource/loot/loot-barrel-2.prefab (blueprint fragments: yes)
  26.940% Nothing
  2.057% syringe.medical (syringe.medical)
  2.057% Bandage (bandage)
  ...
```

## Running

To run the program, run `java` with `LootConfigDump` as the main class, and provide the path to your `LootConfig.json` file as the first argument.

Note that this program runs independently of a Rust server. It is not a Rust or Oxide plugin.

### Prerequisites

You need the following to use this program:

1. A Rust server.
2. The [LootConfig][lootconfig] plugin for [Oxide][oxide].
3. An installation of Java to run the program.

When the LootConfig plugin is started for the first time on a Rust server, it will dump the server's loot configuration into the `LootConfig.json` file. You can also force it to re-dump at any time by deleting that file, then reloading the plugin by using the command `oxide.reload LootConfig` via RCON.

## Output

The program will print the drop chance of all items in all loot containers, for example:

```text
assets/bundled/prefabs/autospawn/resource/loot/loot-barrel-1.prefab (blueprint fragments: yes)
  4.878% Bow And Arrow (arrow.wooden x5, bow.hunting)
```

The first line here is a loot container, and this one also drops blueprint fragments independently of any loot. The second line shows a loot category in the `LootConfig.json` file (`Bow and Arrow`), along with the items in it (5 wooden arrows and a hunting bow.) The percentage is the drop chance of the loot category for the particular loot container.

Blueprint drops are shown in brackets:

```text
2.369% bucket.water Blueprint [bucket.water]
```


[lootconfig]: http://oxidemod.org/plugins/lootconfig.861/
[oxide]: http://oxidemod.org/
