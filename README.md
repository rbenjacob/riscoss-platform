# Introduction

The [RISCOSS project](http://www.riscoss.eu) will offer novel risk identification, management and mitigation tools and methods for community-based and industry-supported OSS development. 

RISCOSS will deliver a decision-making management platform integrated in a business-oriented decision-making framework, which together support placing technical OSS adoption decisions into organizational, business strategy and broader OSS community context.

This is the main repository for the RISCOSS Platform code.

# Project structure

The project structure reflects the structure of the RISCOSS Platform architecture:

* riscoss-platform-dm contains the code of the Domain Manager (DM)
* riscoss-platform-rdr contains the code for the Risk Data Repository (RDR)
* riscoss-platform-analyser is the engine which calculates risk based on data-points stored in the RDR.
* riscoss-platform-jsmile is a wrapper around the proprietary jSmile project which is used by the riscoss-platform-analyser.

Please refer to the [RISCOSS White Paper](http://www.riscoss.eu/bin/download/Discover/Whitepaper/RISCOSS-Whitepaper.pdf) for a more detailed description.


# jSmile

The Riscoss Analyzer requires a proprietary .so library called jSmile in order to function.
Unfortunaltely, we can't give you a copy but here is a hash of the file which you need in order
to run the build (this hash is of the file for Linux/AMD64, use the appropriate equivilant if needed).

    sha256sum ./riscoss-platform-jsmile/src/main/resources/libjsmile.so
    cd1767397a82e1fc52c94982f1ab21c35a0a6cdee2598a12091e3d69311b156c  ./riscoss-platform-jsmile/src/main/resources/libjsmile.so

