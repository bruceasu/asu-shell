var ECHO = function () {
	this.getName = function () {
		return "echo";
	}

	this.execute = function (args) {
//       var index;
//       println(args);
//       var values =args.toArray();
//       println('Java to Javascript:');
//       for(index in values) {
//                 println(values[index]);
//       }
		var values = args.toArray();
		if (args.size() > 0) {
			for (var i = 0, j = values.length; i < j; i++) {
				print(values[i]);
				print(" ");
			}
			println('');
		} else {
			print(args);
		}

		println("");
	}

	this.usage = function () {
		println("echo");
		println("    print what you type.");
	}

	this.executeBg = function (args) {
		this.execute(args)
	}
}

function getCmd() {
	return new ECHO();
}

function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}