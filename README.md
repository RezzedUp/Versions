<!-- Badges Config -->

[maven-central]: https://search.maven.org/artifact/com.rezzedup.util/versions "Maven Central"
[maven-central-badge]: https://img.shields.io/maven-central/v/com.rezzedup.util/versions?color=ok&label=Maven%20Central

[license]: ./LICENSE "Project License: MPL-2.0"
[license-badge]: https://img.shields.io/badge/License-MPL--2.0-blue

[java-version]: # "Java Version: 11"
[java-version-badge]: https://img.shields.io/badge/Java-11-orange

[javadoc]: https://javadoc.io/doc/com.rezzedup.util/versions "View latest javadoc"
[javadoc-badge]: https://javadoc.io/badge2/com.rezzedup.util/versions/javadoc.svg?label=Javadoc&color=%234D7A97

<!-- Header + Badges -->

# 🎯️ Versions

[![][maven-central-badge]][maven-central]
[![][license-badge]][license]
[![][java-version-badge]][java-version]
[![][javadoc-badge]][javadoc]

Semantic versioning.

## Maven

```xml
<dependency>
    <groupId>com.rezzedup.util</groupId>
    <artifactId>versions</artifactId>
    <version><!--release--></version>
</dependency>
```

### Versions

Find available versions on the releases page of this repository.

<!-- Maven Central: https://search.maven.org/artifact/com.rezzedup.util/versions -->

<details id="note-snapshot-versions">
<summary><b>Note:</b> <i>Snapshot Versions</i></summary>

> [ℹ️](#note-snapshot-versions)
> Snapshot releases are available at the following repository:
>
> ```xml
> <repositories>
>     <repository>
>         <id>ossrh-snapshots</id>
>         <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
>     </repository>
> </repositories>
> ```
</details>

<details>
<summary><b>Note:</b> <i>Shading</i></summary>

> [ℹ️](#note-shading)
> If you intend to shade this library, please consider **relocating** the packages
> to avoid potential conflicts with other projects. This library also utilizes
> nullness annotations, which may be undesirable in a shaded uber-jar. They can
> safely be excluded, and you are encouraged to do so.
</details>
