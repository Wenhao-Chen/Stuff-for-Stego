Setting up PixelKnot:
1. pull PixelKnot repo
2. go to project folder and run "git submodule update --init --recursive"
3. link the jni files in F5Android (using ndk-build)

Customizing PixelKnot:
1. add the two java files - Dummy.java and StegoDBActivity.java
2. change main activity to StegoDBActivity
3. customize the embedding process in StegoDBActivity
