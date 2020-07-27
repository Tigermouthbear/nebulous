# Nebulous
A Java bytecode obfuscator written in Kotlin. As of now, it must be used in a development environment

## List of transformers
- StringPooler
- StringEncryptor
- StringSplitter (very stupid)
- NumberPooler
- FieldRenamer
- MethodRenamer
- ClassRenamer
- MemberShuffler
- FullAccessFlags
- DebugInfoRemover
- NOPRemover
- LineNumberRemover
- GotoInliner
- GotoReturnInliner

## Credit
Credit to [jasmo](https://github.com/CalebWhiting/java-asm-obfuscator) and [radon](https://github.com/ItzSomebody/radon) for being helpful to learn obfuscation techniques
