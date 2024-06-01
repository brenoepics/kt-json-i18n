# Kotlin JSON Internationalization (1.1.0)

![Java](https://img.shields.io/badge/java-%3E%3D8-blue)
![Kotlin](https://img.shields.io/badge/kotlin-%3E%3D2.0.0-blue)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=brenoepics_json-translate&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=brenoepics_json-translate)

An internationalization tool that translates JSON files using different translation APIs.

### Supported API's

- [x] Microsoft Azure Translator [AT4J](https://github.com/brenoepics/at4j)
- [ ] Google Translator
- [ ] DeepL Translator

## Getting Started

### Prerequisites

- Java 8 or higher
- Kotlin 2.0.0 or higher

### Usage

1. Download latest version from [releases](/releases/latest) and unzip.
2. Rename `config.ini.example` to `config.ini`.
3. Fill in the `config.ini` file with your Azure credentials, input/output paths, and other settings.
4. Run the project using the command `java -jar kt-json-i18n-1.1.0-jar-with-dependencies.jar`

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Support

If you find this project useful, you can show your support by giving it a ⭐ on GitHub!

## License

This project is distributed under the [Apache license version 2.0](./LICENSE).
