# ArenaShooter

A PaperMC plugin turning a Minecraft server into a fast-paced boomer arena shooter.

## Features

- Custom weapons
- Different game modes
- Powerups
- Player statistics
- Cosmetics
- Interface for creating arenas/maps
- Per-game customization
- Multi-language support: `en`, `et`, `pt_br`, `ru`, `uk`

## Setting up an instance

1. As the plugin expects all arenas to **be in separate worlds**, you may want to set up a world managing plugin, such
   as Multiverse-Core.
2. Download or build the plugin, gather the following dependencies: **JellyLib**, **JellyGuiApi**, **ProtocolLib**.
3. Set up lobby location in the `config.yml`.
4. Build the map and import it using `/arena create` command. *Note: world name has to be the same as arena name*
5. Configure arenas using `/arena set` commands.

## Resource Pack

Though technically not required, it is highly recommended to use a resource pack built for this game mode. The official
one is: [ArenaShooterResourcePack](https://github.com/EdenorMC/ArenaShooterResourcePack)

## Public instance

*Coming soon...*