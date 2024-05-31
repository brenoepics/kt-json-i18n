# JSON Translator for Java (1.0)

![Java](https://img.shields.io/badge/java-%3E%3D8-blue)
![Kotlin](https://img.shields.io/badge/kotlin-%3E%3D2.0.0-blue)

This project is a JSON translator that uses different translation APIs to translate JSON files.

## Features

- Translation API management: The project includes a `TranslationManager` class that manages different translation APIs. It creates the APIs and stores them in an EnumMap.
- Translation API building: The `ApiBuilder` class is an abstract class for building translation APIs. It currently includes an implementation for Azure.
- JSON translation: The `Translate` class is the main class that starts the translation process. It reads an ini file and starts the translation process.
- Custom indentation, output template...

### Supported API's

- [x] Microsoft Azure Translator [AT4J](https://github.com/brenoepics/at4j)
- [ ] Google Translator
- [ ] DeepL Translator

## Getting Started

### Prerequisites

- Java 8 or higher
- Kotlin 2.0.0 or higher

### Usage

1. Download latest version from [releases](https://github.com/brenoepics/json-translate/releases/latest) and unzip.
2. Update and rename `config.ini.example` to `config.ini`.
3. Run the project using the command `java -jar json-translator-1.0-jar-with-dependencies.jar`
   
## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Support

If you find this project useful, you can show your support by giving it a ⭐ on GitHub!

## License

This project is distributed under the [Apache license version 2.0](./LICENSE).
