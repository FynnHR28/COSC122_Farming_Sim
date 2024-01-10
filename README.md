# 2D Farming Simulator
<img width="1053" alt="Screenshot 2024-01-06 at 4 01 11 PM" src="https://github.com/FynnHR28/COSC122_Farming_Sim/assets/136636012/5c694703-c14a-4799-8175-62e245897a52">

## Description
This is the program I created for my final project in my Introductory to Computer Science II course at Amherst College. The code involves concepts like subclassing, interfaces, and advanced built-in data structures learned in the class. All graphics are original, made on Piskel.com or Adobe Photoshop. It has elements inspired by Minecraft.

## Compile and Run
Compile and run this program with:
javac *.java
java Main

## Instructions for Use
NOTE that all these instructions are also available through guided user action in the game

To begin, click the play button on the title screen.

Moving Player around: the classic 'w''a''s''d' keyboard input moves the character around

Opening the inventory: type 'i'. This pauses the game and allows you to move items around in inventory and tool bar by clicking one item, and then clicking another item slot (which can be open or occupied).

Closing the Inventory: type 'esc'. This resumes the game gets rid of the inventory graphics on the screen. You can only swap items around when the inventory is up on the screen.

Your toolbar: This is the row of slots at the bottom of the screen. These are accessed by pressing number keys. 0 for the first one, 1 for the second one, and so on. Doing so equips the item (if there is one) in that toolbar slot. The equipped slot is highlighted blue. While having an equipped item you can do a few things:
if it is a crop you can plant that crop while clicking on a soil tile while that tile is the one that is in front of the player (shown by the white rectangle highlight).
If you equip the fishing rod you can fish when clicking on a water tile (while it is highlighted in front of you).
Doing so starts a fishing event which continues to update the AI animals and crops growing, but stops the player from being able
to move around while fishing. Additional things include placing animal traps on grass tiles while they are highlighted. If a bunny walks over this trap a dialogue box pops up telling that you caught a bunny and a new one spawns.
NOTE to have an equipped item you must first move an item from the inventory into the toolbar, and then press the corresponding key on the keyboard that equips that toolbar slot you moved the item to.

Your house: you can walk into the house on the map just by walking into it, and you can exit by walking into the southern most tip of the interior.
While inside the house: You can also customize the look of the farm by clicking on the table in the middle of it while it is highlighted. This activates a creative mode where you can move around tree, soil, and grass tiles by clicking on two of them, causing a swap. Note you cannot swap soil tiles when they have a crop growing in them, nor can you swap any of the other tiles.

To reiterate, in this game you can move your items around freely, equip whatever you want, and complete various activities like planting
Crops in soil, harvesting said crops, fishing, trapping bunnies, and customizing your farm. The game itself has guides built into it to help the player learn how to do things. Typing 'h' while playing opens a help menu, and the inventory also includes some info about switching items. The key thing to remember is you can only trigger an interaction with a tile if you are clicking with the mouse within the tile that is in front of you, which is highlighted with a white rectangle.

