<<<<<<< HEAD

# Constellation
Decentralized Application Integration Platform.

This repository is the reference implementation of our 
[DAG](https://en.wikipedia.org/wiki/Directed_acyclic_graph) 
protocol using the 
[Scala](https://www.scala-lang.org/) 
programming language.

```scala
constellation
└── src
    ├── it                                   <─── continuous integration
        ├── ClusterDebug.scala
        ├── ...
        └── UploadChain.scala
    ├── main/scala/org/constellation         <─── protocol implementation
        ├── consensus
            ├── Consensus.scala
            ├── EdgeProcessor.scala
            └── ...
        ├── crypto
            ├── Base58.scala
            ├── KeyUtils.scala
            └── Wallet.scala
        ├── p2p
            └── ...
        ├── primitives
            ├── ...
            └── TransactionValidation.scala
        ├── serializer
        ├── util
            ├── API.scala
            ├── ConstellationNode.scala
            ├── Data.scala
            └── LevelDB.scala
    └── test/scala/org/constellation          <─── unit tests
        ├── app
        ├── cluster
        ├── consensus
        ├── p2p
        ├── rpc
        ├── ...
        └── UtilityTest.scala
└── ui
```

(Not shown in the above table are things like fixtures, shell- and sbt-scripts or markup-, package-, scala-project and config-files that are used for project building, deploying, docker, kubernetes, network setup, unit tests, continuous integration, and so on.)

## :computer: Building for development
### On Linux and Mac
1. Check out the repository 
```bash
git clone git@github.com:Constellation-Labs/constellation.git
```
2. From root directory `constellation`, run 
```bash
./build.sh
```
or optionally (to connect to other host)
```bash
./build.sh seedhost:port
```
3. Interact with app via the API 
By default this is 
http://localhost:9000. 
Look at the file _API.scala_ for endpoints.

### Docker execution 

Note: *For now this installation is not covered here in detail.*

#### Install docker locally
1. Set up [docker](https://www.docker.com/).
2. Follow directions for building for development.
3. Run
```bash
sbt docker:publishLocal
```
4. Run
```bash
./run-local-docker.sh
```

We will publish the corresponding images to dockerhub soon.

For Windows, just grab the docker ```cmd``` from the file and run directly.

### Vagrant execution
This is **deprecated** but may be useful for people running Windows, etc.:
1. Download [vagrant](https://www.vagrantup.com).
2. Run 
```bash
vagrant up
```
from project directory. See also
* [Vagrant setup for Windows](https://drive.google.com/file/d/1xobpv4Ew1iCN9j-M-ItU6PsfnybHUryy/view)
* [Vagrant setup for Ubuntu](https://docs.google.com/document/u/1/d/e/2PACX-1vST7vBIMxom99hKr5XyVFpM6TAs_pw-iqq403AktMWnqr3dxUFX5c0g9BWD5gU5TDPZVXKcW3HTWbVl/pub)

## :green_book: API Docs
### WIP 
We intend to use **Swagger** or similar to publish comprehensive API docs.

## :rotating_light: Troubleshooting
Should you discover a bug, please open a ticket using the github issue function. If you don't know the resolution, follow standar github reporting guidelines, stating your system, what you did and tried. 

If you run into issues during the installation and for general software support, best make a thread on the [community portal **Orion**](https://orion.constellationlabs.io/accounts/login/?next=/) or ask a quick question on the Constellation [discord](https://discordapp.com/invite/KMSmXbV) server. 

---

For more documentation and developer tools, as well a community constributions :two_hearts:, contribution guidelines and so on, you can find links to the community outlets in the 
[**resource list**](https://github.com/Constellation-Labs/awesome-constellation). 
=======
## Constellation
This repository is the reference implementation of our **Decentralized Application Integration Platform** using the 
[Scala](https://www.scala-lang.org/)
programming language. We build a horizontally scalable 
[DAG](https://en.wikipedia.org/wiki/Directed_acyclic_graph) 
protocol that you can build upon and interface with using common software standards.

### :books: Resources
For developer tools and documentation and consult the 
[**/docs**](https://github.com/Constellation-Labs/constellation/tree/dev/docs) 
or the
[**/docs-gh-pages**](https://github.com/Constellation-Labs/constellation/tree/dev/docs-gh-pages)
hosted on our
[docs website](https://constellation-labs.github.io/constellation/).
For motivations and explanations of the project in context of the crypto space, visit the
[**wiki**](https://github.com/Constellation-Labs/constellation/wiki).

```scala
src
├── it/scala/org/constellation           <─── continuous integration
├── main/scala/org/constellation         <─── protocol implementation
    ├── consensus
          Consensus.scala
    ├── crypto
          KeyUtils.scala
    ├── p2p
    ├── primitives
    ├── serializer
    └── util
          API.scala
          ConstellationNode.scala
└── test/scala/org/constellation          <─── unit tests
```

### :computer: Build instructions
For details on the build process, as well as pointers for docker, vagrant and deployment, see [/docs/build-instructions](https://github.com/Constellation-Labs/constellation/blob/dev/docs/build-instructions.md).

### :green_book: API Docs
We intend to use **Swagger** to publish comprehensive API docs.

### :rotating_light: Troubleshooting
For issues and bug reports, see [wiki/Contribution-guidelines](https://github.com/Constellation-Labs/constellation/wiki/Contribution-guidelines). 
There you also find general pointers toward the development process. Note that the `master` branch might be behind the `dev` branch by a few weeks.

### :two_hearts: Community
For questions and contributions, can find links to the community outlets and more in the 
[**resource list**](https://github.com/Constellation-Labs/awesome-constellation). 
Our knowledge- and news-outlet is the 
[**Orion**](https://orion.constellationlabs.io/) 
Discourse forum. To quickly reach out, join our 
[Discord](https://discordapp.com/invite/KMSmXbV) 
server for a chat:

  <a href="https://discordapp.com/invite/KMSmXbV">
	  <img src="https://img.shields.io/badge/chat-discord-brightgreen.svg"/>
  </a>
>>>>>>> 5b8454d382daf64da03256ca527aad49a413f430
