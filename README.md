# Plate Validator DFA 

A Turkish car plate validation system built with **Kotlin**, using the **State Pattern** and **Deterministic Finite Automaton (DFA)** design principles.

![dfa](https://github.com/user-attachments/assets/dff0d7f5-71a0-451c-a286-6acef5f924d4)

## Overview

This project validates Turkish vehicle license plates by implementing a finite state machine.  Turkish license plates follow a specific format, and this validator ensures that input strings conform to that structure using a state-driven approach.

![Screen_recording_20260106_165921-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/daabfe57-3975-4e7a-ada9-5b0c5717cf6d)

### Turkish License Plate Format
Turkish car plates typically follow this pattern:
- **2 Number** (region code)
- **1-3 letters** (sequential number)
- **if 1 letter => 2-3 number** 
- **if 2 letter => 3-4 number**
- **if 3 letter => 4-5 number** 
Example: `34 AA 123` or `16 S 23256`

## Architecture

### Design Patterns

**State Pattern**: Each character position in the plate is represented as a different state, allowing the validator to transition through states sequentially.

**DFA (Deterministic Finite Automaton)**: A mathematical model that defines: 
- **States**: Different positions/stages of plate validation
- **Transitions**: Rules for moving between states based on input character type
- **Accept States**: Valid completion states

