
<!DOCTYPE html>
<html>
<head>
    <title>Rob&sup3; Wordlist Sorter Stats</title>
</head>
<body>


<p>STATS PAGE</p>

<p>The total unique is ${stats.totalUnique?html}</p>

<ul>
  <#list stats.topTen as top>
    <li>${top.word} for ${top.timesUsed}</li>
  </#list>
</ul>

<ul>
  <#list stats.bottomTen as bottom>
    <li>${bottom.word} for ${bottom.timesUsed}</li>
  </#list>
</ul>

${tracking.totalLinesSubmitted?html} - total lines submitted<br/>
${tracking.totalFilesSubmitted?html} - total files submitted<br/>
${tracking.averageLinesSubmitted?html} - average lines per file<br/>
${tracking.mostLinesSubmitted?html} - most lines submitted in one file<br/>

</body>
</html>