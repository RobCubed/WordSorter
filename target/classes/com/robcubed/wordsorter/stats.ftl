<!DOCTYPE html>
<html>
<head>
    <title>Rob&sup3; Wordlist Stats</title>
    <link rel="stylesheet" href="/assets/stylesheet.css"/>
</head>
<body>

<div class="primary">
    <div class="title">rob&sup3; Wordlist Stats</div>
    <div class="options">
Unique lines in database: ${stats.totalUnique?html}<br/>
Total lines submitted: ${tracking.totalLinesSubmitted?html}<br/>
Files submitted: ${tracking.totalFilesSubmitted?html}<br/>
Average lines per file: ${tracking.averageLinesSubmitted?html}<br/>
Most lines submitted in one file: ${tracking.mostLinesSubmitted?html}<br/>
</div>


    <div class="title-small">Most Common Words</div>

    <div class="options">
<p><ol class="toplist">
  <#list stats.topTen as top>
    <li>${top.word} for ${top.timesUsed}</li>
  </#list>
</ol>
</div>

    <div class="title-small">Most Uncommon Words</div>

    <div class="options">
<ol class="toplist">
  <#list stats.bottomTen as bottom>
    <li>${bottom.word} for ${bottom.timesUsed}</li>
  </#list>
</ol>
</p>


    </div>
    <div class="title-small"><a href="/">WordSorter Main</a></div>


</div>

</body>
</html>
