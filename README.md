# mansave-minigame
[![Java CI with Maven](https://github.com/MadeByIToncek/Mansave/actions/workflows/maven.yml/badge.svg)](https://github.com/MadeByIToncek/Mansave/actions/workflows/maven.yml)

This plugin is inspired by YouTuber McBirken.

TODO: Fill out this long description.

## Table of Contents

- [Security](#security)
- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Security

## Install

```
1. Go to the [Releases](https://github.com/MadeByIToncek/Mansave/releases) page.
2. Download newest Mansave-*.*.jar.
3. Download [Paper](https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/443/downloads/paper-1.16.5-443.jar) and run it once. (If you don't know how, google it)
4. Move Mansave-*.*.jar into `plugins`
5. Start the server
```

## Usage

* `/mansave start` - Spustí hru
* `/mansave stop` - Ukončí hru
* `/mansave setHunterItems` - Otevře menu editace hotbaru
* `/mansave addHunter <hráč>` - Přidá hráče do seznamu lovců
* `/mansave removeHunter <hráč>` - odebere hráče ze seznamu lovců
* `/mansave huntersTakeDamage <true|false>` - false = lovci si nemůžou ubrat damage
* `/mansave huntersHungerLoss <true|false>` - false = lovci neztrácí hlad.
* `/mansave suicidalHungerLoss <true|false>` - false = speedrunnerovi neubývá hunger
* `/mansave countdownInSeconds <sekundy>` - časový limit (normálně 300s = 5min)

## Maintainers

[@MadeByIToncek](https://github.com/MadeByIToncek)

## Contributing

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 IToncek
