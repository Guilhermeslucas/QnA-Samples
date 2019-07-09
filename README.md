# QnA Maker Seeder

This project demonstrates the usage of QnA maker through its REST APIs using Java.

## How it works

1. Users update an Excel file with question and answer pairs.
2. The "Seeder" reads the file and creates a request body.
3. The request is pushed to QnA Maker service.

After "seeding" the QnA Knowledge Base, it's ready for consumption. This repository also contains sample code showing how to consume the QnA Maker REST APIs using Java.

## How to run

Create a ``src/main/java/resources/qna.properties`` file with the following contents:

```key
BaseEndpoint=your-qna-base-endpoint
KeyVaultEndpoint=your-azure-key-vault-endpoint
```

Install or make sure you have Java and Gradle.

```bash
java --version
gradle --version
```

Ready to go

```bash
gradle run
```

or

```bash
gradle test
```
