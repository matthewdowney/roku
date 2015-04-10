Quick overview
==============
<ul>
	<li>Roku.java - Class for interacting directly with a Roku box via HTTP requests.</li>
	<li>Scan.java - Class for finding Roku devices on the network via SSDP requests.</li>
	<li>console.java - Class responsible for managing user input and making the correct HTTP requests to the Roku.</li>
	<li>x.java - A GUI window so the user can use the arrow keys to control the Roku.</li>
</ul>

#Contributing
Right now the `$ find all` command (inside of Console.java) calls the Scan class 20 times looking for new unique IP Addresses.
Perhaps this could be implimented more efficiently.
