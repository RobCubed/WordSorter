<html>

<head>
	<title>Test</title>
	<script src="http://192.168.1.128:7050/assets/dropzone.js"></script>
</head>

<body>
		<table border="1">
			<tr>
				<th colspan="2">Word (${word.id})</th>
			</tr>
			<tr>
				<td>Word</td>
				<td>${word.word?html}</td>
			</tr>
			<tr>
				<td>Times Used</td>
				<td>${word.timesUsed}</td>
			</tr>
		</table>
		
		<form action="/file-upload"
      class="dropzone"
      id="my-awesome-dropzone">
      
      <input type="file" name="file" />
      </form>
      
		</body>
		</html>