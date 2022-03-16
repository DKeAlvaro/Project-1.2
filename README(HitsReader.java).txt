HIT READER:
-To use it, create an object of the class HitsReader.java and use that object to call the method read(),
with the parameter being the path of the file that you want to read. Example:
        HitsReader test = new HitsReader();
        test.read("YourPath");
It will return a 2d array containing the coordinates of the hits presented in the text file.

FORMAT OF THE TEXT FILE:
-In order for the reader to work properly, check the example below:
            x = 0.5, y = 1.0
            x = 1.0, y = 2
            x = 3.5, y = 6.5
