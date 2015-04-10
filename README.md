  Installation  
================
#Linux
To install, simply run `$ sudo ./install` inside of the install directory.
To uninstall you may navigate to the directory it installs to (/etc/roku) and run `$ sudo ./uninstall`.

#Other OS
This was written with *nix users in mind, but it can be used on other systems. The components are written in Java, so any computer that has the JRE installed can run this by saving Roku.jar somewhere and accessing it with `java -jar Roku.jar`.

  Use  
=======
Once installed, simply run `$ roku find` to scan your lan and connect to the first roku found.

You can also run `$ roku <ip address>` to connect to a specific ip address manually, or just `$ roku` to drop into the roku shell.

If you have multiple roku devices, run the command `$ roku` to drop into the shell and then use `$ find all` to scan your network for multiple devices.
If the scan misses one of the devices you're looking for, try running it again, the protocol Roku uses to identify itself (SSDP) can be inconsistent.

Now that you're in the shell (connected or otherwise), run `$ help` for an overview of the commands available. 

Simply run the command "x" to control the currently conntected roku with the arrow keys and enter.

Feel free to contact me with any questions, bugs, or suggestions. 
