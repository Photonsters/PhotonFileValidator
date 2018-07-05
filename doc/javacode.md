# Java Style Guide

## Introduction
This document serves as the complete definition of our coding standards for source code in the Java™ Programming Language. A Java source file is allowed only if it adheres to the rules herein.

Like other programming style guides, the issues covered span not only aesthetic issues of formatting, but other types of conventions or coding standards as well.

## File names
The source file name consists of the case-sensitive name of the top-level class it contains (of which there is exactly one), plus the .java extension.

## File encoding
Source files are encoded in UTF-8.

## Characters
Aside from the line terminator sequence, the ASCII horizontal space character (0x20) is the only whitespace character that appears anywhere in a source file. This implies that:

All other whitespace characters in string and character literals are escaped.
Tab characters are not used for indentation.

For any character that has a special escape sequence (\b, \t, \n, \f, \r, \", \' and \\), that sequence is used rather than the corresponding octal (e.g. \012) or Unicode (e.g. \u000a) escape.

# Source File structure
A source file consists of, in order:

- License or copyright information, if present
- Package statement, The package statement is not line-wrapped
- Import statements, Import statements are not line-wrapped.
- Exactly one top-level class

Exactly one blank line separates each section that is present.

# Class definition

Exactly one top-level class declaration
Each top-level class resides in a source file of its own.

## Ordering of class contents
The order you choose for the members and initializers of your class can have a great effect on learnability. However, there's no single correct recipe for how to do it; different classes may order their contents in different ways.

What is important is that each class uses some logical order, which its maintainer could explain if asked. For example, new methods are not just habitually added to the end of the class, as that would yield "chronological by date added" ordering, which is not a logical ordering.

## Overloads: never split
When a class has multiple constructors, or multiple methods with the same name, these appear sequentially, with no other code in between (not even private members).


# Formatting

## Braces

### Braces are used where optional
Braces are used with if, else, for, do and while statements, even when the body is empty or contains only a single statement.

### Nonempty blocks: K & R style
Braces follow the Kernighan and Ritchie style ("Egyptian brackets") for nonempty blocks and block-like constructs:

- No line break before the opening brace.
- Line break after the opening brace.
- Line break before the closing brace.
- Line break after the closing brace, only if that brace terminates a statement or terminates the body of a method, constructor, or named class. For example, there is no line break after the brace if it is followed by else or a comma.


### Block indentation: +4 spaces
Each time a new block or block-like construct is opened, the indent increases by two spaces. When the block ends, the indent returns to the previous indent level. The indent level applies to both code and comments throughout the block.

### One statement per line
Each statement is followed by a line break.

### Indent continuation lines at least +8 spaces
When line-wrapping, each line after the first (each continuation line) is indented at least +8 from the original line.

When there are multiple continuation lines, indentation may be varied beyond +8 as desired. In general, two continuation lines use the same indentation level if and only if they begin with syntactically parallel elements.


# Naming
## Rules common to all identifiers
Identifiers use only ASCII letters and digits (underscores not allowed)

## Package names
Package names are all lowercase, with consecutive words simply concatenated together (no underscores).

## Class names
Class names are written in UpperCamelCase.

Class names are typically nouns or noun phrases. For example, Character or ImmutableList. Interface names may also be nouns or noun phrases (for example, List), but may sometimes be adjectives or adjective phrases instead (for example, Readable).

## Method names
Method names are written in lowerCamelCase.

Method names are typically verbs or verb phrases. For example, sendMessage or stop.

## Constant names
Constant names use CONSTANT_CASE: all uppercase letters, with each word separated from the next by a single underscore.

## Non-constant field names
Non-constant field names (static or otherwise) are written in lowerCamelCase. These names are typically nouns or noun phrases.

## Parameter names
Parameter names are written in lowerCamelCase. One-character parameter names in public methods should be avoided.

## Local variable names
Local variable names are written in lowerCamelCase. Even when final and immutable, local variables are not considered to be constants, and should not be styled as constants.

