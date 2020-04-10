# Seam Carving
Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time. A vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row; a horizontal seam is a path of pixels connected from the left to the right with one pixel in each column. Below left is the original 505-by-287 pixel image; below right is the result after removing 150 vertical seams, resulting in a 30% narrower image. Unlike standard content-agnostic resizing techniques (such as cropping and scaling), seam carving preserves the most interest features (aspect ratio, set of objects present, etc.) of the image.

Although the [underlying algorithm](https://www.youtube.com/watch?v=6NcIJXTlugc) is simple and elegant, it was not discovered until 2007. Now, it is now a core feature in Adobe Photoshop and other computer graphics applications.

![image-full](https://coursera.cs.princeton.edu/algs4/assignments/seam/HJoceanSmall.png) ![image-cropped](https://coursera.cs.princeton.edu/algs4/assignments/seam/HJoceanSmallShrunk.png)

In this assignment, you will create a data type that resizes a W-by-H image using the seam-carving technique.

Finding and removing a seam involves three parts and a tiny bit of notation:

 - Notation. 
 
 In image processing, pixel (x, y) refers to the pixel in column x and row y, with pixel (0, 0) at the upper left corner and pixel (W − 1, H − 1) at the bottom right corner. This is consistent with the Picture data type in algs4.jar. Warning: this is the opposite of the standard mathematical notation used in linear algebra where (i, j) refers to row i and column j and with Cartesian coordinates where (0, 0) is at the lower left corner.

        a 3-by-4 image

        (0, 0)      (1, 0)     (2, 0) 
        (0, 1)      (1, 1)     (2, 1)  
        (0, 2)      (1, 2)     (2, 2)  
        (0, 3)      (1, 3)     (2, 3)   
  
 
We also assume that the color of a pixel is represented in RGB space, using three integers between 0 and 255. This is consistent with the [java.awt.Color](http://docs.oracle.com/javase/7/docs/api/java/awt/Color.html) data type.

1- Energy calculation

The first step is to calculate the energy of each pixel, which is a measure of the importance of each pixel—the higher the energy, the less likely that the pixel will be included as part of a seam (as we'll see in the next step). In this assignment, you will implement the dual-gradient energy function, which is described below. Here is the dual-gradient energy function of the surfing image above:

![gradient](https://coursera.cs.princeton.edu/algs4/assignments/seam/HJoceanSmallEnergy.png)


The energy is high (white) for pixels in the image where there is a rapid color gradient (such as the boundary between the sea and sky and the boundary between the surfing Josh Hug on the left and the ocean behind him). The seam-carving technique avoids removing such high-energy pixels.

2- Seam identification

The next step is to find a vertical seam of minimum total energy. This is similar to the classic shortest path problem in an edge-weighted digraph except for the following:

  - The weights are on the vertices instead of the edges.
  - We want to find the shortest path from any of the W pixels in the top row to any of the W pixels in the bottom row.
  - The digraph is acyclic, where there is a downward edge from pixel (x, y) to pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1), assuming that the coordinates are in the prescribed range.
   
![seam](https://coursera.cs.princeton.edu/algs4/assignments/seam/HJoceanSmallVerticalSeam.png)

3- Seam removal

The final step is to remove from the image all of the pixels along the seam.


***

Full specification found here:
https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php

See also:

https://lift.cs.princeton.edu/java/linux/ for libs and steps to install
***

  `javac-algs4 SeamCarver.java `
  
  `spotbugs SeamCarver.class` 
  
  `pmd SeamCarver.java` 
  
  `checkstyle -coursera SeamCarver.java` 

***
