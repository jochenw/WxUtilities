<html>
  <head>
    <title>ISCCR - Single package scan</title>
    <script type="text/javascript">
        document.getElementById('form').onsubmit = function (evt) {
    	   let sel = document.getElementById('packageName').value;
    	   if (sel.value.length == 0) {
    	      alert("Select an IS package, please")
    	      evt.preventDefault();
    	   } 
    	}
    </script>
  </head>
  <body>
    <h1>ISCCR - Single package scan</h1>
    <p>This page allows you to launch a scan for a single IS package,
      that you can select below:</p>
    <p>
      <form id="form" method="get" action="/rest/wx/rmtadmin/ws/isccr/singlePackageScan">
        <label for="packageName">Select the package:</label>
        <select name="packageName" id="packageName">
            <option value="">Select...</option>
          %invoke wx.rmtadm.impl.ui.isccr:getScannablePackages%
          %loop packageNames%
            <option value="%value encode(Xml)%">%value encode(Xml)%</option>
          %end%
          %end%
        </select><br></br>
        <label for="outputType">Select the output type:</label><br></br>
          <input type="radio" name="outputType" value="" id="outputType" checked>Summary HTML</input><br></br>
          <input type="radio" name="outputType" value="details">Details HTML</input><br></br>
          <input type="radio" name="outputType" value="detailsXml">Details XML</input><br></br>
          <input type="radio" name="outputType" value="detailsCsv">Details CSV</input><br></br>
          <input type="radio" name="outputType" value="zip">All output as a zip file</input><br></br>
        <label for="submit">Start the scan</label>
        <input id="submit" type="submit" value="Now!"></input>
      </form>
    </p>
  </body>
</html>