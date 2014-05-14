
<!DOCTYPE html>
<html>
<head>
    <title>Rob&sup3; Wordlist Sorter</title>
    <link rel="stylesheet" href="/assets/stylesheet.css"/>
</head>
<body>

<form action="/words/upload" method="post" enctype="multipart/form-data">
<div class="primary">
    <div class="title">rob&sup3; Wordlist Sorter</div>
        <div class="dropzone">
            <input name='file' type="file" multiple="multiple" />
        </div>
    <div class="options">
        <div class="options-left">
            <ul>
                <li class="checkbox"><input id="combine_files" type="checkbox" name="combine_files" value="combine_files">
                    <label for="combine_files">Combine Files</label></li>
                <li class="checkbox"><input id="no_duplicates" type="checkbox" name="no_duplicates" value="no_duplicates">
                    <label for="no_duplicates">Remove Duplicates</label></li>
                <li class="radio"><input id="a_to_z" type="radio" name="sorting" value="a_to_z">
                    <label for="a_to_z">Sort A-Z</label></li>
                <li class="radio"><input id="z_to_a" type="radio" name="sorting" value="z_to_a">
                    <label for="z_to_a">Sort Z-A</label></li>
                <li class="radio"><input id="randomize" type="radio" name="sorting" value="randomize">
                    <label for="randomize">Randomize</label></li>
            </ul>
        </div>
        <div class="options-right">
            <div class="more-info"><span class="instructions">Drag file(s) to the gray box above, or 'browse' and select one or more files.<br/>
            All tabs and leading/trailing whitespace will be removed. Max line length 255.</span>
<br/>


            </div>
        </div>
        <div class="process"><input type="submit" value="PROCESS" class="process_button"/></div>
        <div><span class="instructions-note">Note: You must drag or select multiple files simultaneously. Dragging one after another will overwrite the previous file(s).</span></div>
    </div>

</div>
</form>

</body>
</html>