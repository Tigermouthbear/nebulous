# Nebulous
A simple and small Java bytecode obfuscator written in Kotlin.

## List of transformers
- StringPooler
- StringEncryptor
- StringSplitter (very stupid, shouldn't use except for testing)
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

## Use
To run nebulous, you must first create a json config file with the template below. You can omit any transformers and they will be automatically set to false.

Example config.json:
```
{
    "input": "input.jar",
    "output": "output.jar",
    "libraries": [
    	"/path/to/external/dependency/"
    ],
    "exclusions": [
        "some/package/path/",
        "some/actual/Class"
    ],
    "StringPooler": true,
    "StringEncryptor": true,
    "NumberPooler": true,
    "FieldRenamer": true,
    "MethodRenamer": true,
    "ClassRenamer": true,
    "ClassRenamerPrefix": "dev/tigr/ares/",
    "MemberShuffler": true,
    "FullAccessFlags": true,
    "DebugInfoRemover": true,
    "NOPRemover": true,
    "LineNumberRemover": true,
    "GotoInliner": true,
    "GotoReturnInliner": true
}
```

Then to run it:
```
java -jar nebulous.jar config.json
```

## Credit
Credit to [jasmo](https://github.com/CalebWhiting/java-asm-obfuscator) and [radon](https://github.com/ItzSomebody/radon) for being helpful to learn obfuscation techniques
