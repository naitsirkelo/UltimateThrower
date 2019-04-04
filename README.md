# UltimateThrower

![Build Status](https://img.shields.io/badge/build-running-green.svg)

## How To Use

- Reset button: Resets the recorded data and prepares for new throw.
- New Throw button: Resets data except Height Record before new throw.
- Settings button: Moves the user to the Settings page where the variable for Minimum Acceleration required can be set using a slider.

## Structure
- Animation: A simple animation will show the ball in motion until it reaches the ground.
- Sound: Plays when ball reaches highest point.
- Forces: The Current force being experienced by the phone and the force the ball was Thrown at are recorded and shown at the bottom of the screen.
- The code is placed in two .java files: MainActivity and Settings. The icon and picture for animation is placed in their respectable folders in ''/res'.


## Checklist

- [x] The git repository URL is correctly provided, such that command works: git clone https://github.com/naitsirkelo/UltimateThrower.git

- [x] The code is well, logically organised and structured into appropriate classes. Everything should be in a single package.

- [x] The app has been user tested with someone other than the author.

- [x] The user can go to Preferences and set the MIN_ACC value (sensitivity).

- [x] The app plays sounds on the ball highest point.

- [x] The app records the highest point reached by the ball. (Bug: Sometimes plays sound when ball hits the ground as well. Could be useful in some cases... maybe...)

- [ ] Share your sensitivity constant as well as the size of the sliding window with others. (Not completed.)

- [x] The git repository URL is correctly provided, such that command works: git clone <url>

- [x] The repo is public, such that reviewers can access it and review it.

- [x] The repo has a README.md file, explaining how to build the app, how to use the app, which features are working, which are not working, how the code is organised, and what extras does the project have.
