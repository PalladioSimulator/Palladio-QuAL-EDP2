# Palladio QuAL EDP2
The Experiment Data Persistency & Presentation (EDP2) framework is a piece of software to support storage, evaluation and presentation of experiment data from within Eclipse. EDP2 is realized using EMF and is to replace the Sensorframework, which is currently employed by the PCM-Bench. 

There are three basic types of data used by EDP2:
* Repositories for storing data in a persistent way
* Metadata describing formats and parameters
* Measurements containing the observed values

EDP2 is to provide the possibility to display these data using different mechanisms. EDP2 requires: The Eclipse-Plugin PCM Development/EDP2/Visualization and the Apache Commons Codec Plug-in in version 1.4.0. EDP2 is available at [SVN](https://svnserver.informatik.kit.edu/i43/svn/code/EDP2/trunk). The following tables provides an overview about the plug-ins and their content.

| Plug-in Name | content |
|---|---|
| `de.uka.ipd.sdq.edp2.examples[/LocalRepository]`	| Example model generation code, model instance and data. |
| `de.uka.ipd.sdq.edp2[.edit,.editor]` | EDP2 model and model handling code. |
| `de.uka.ipd.sdq.edp2.transformation` | Chainable data transformation elements: Adapters and Filters. |
| `de.uka.ipd.sdq.edp2.visualization` |	Graphical user interface including editors. |
| `org.apache.commons.codec` |Apache Commons Codec Plug-in in version 1.4.0. |

## Support
For support
* visit our [issue tracking system](https://palladio-simulator.com/jira)
* contact us via our [mailing list](https://lists.ira.uni-karlsruhe.de/mailman/listinfo/palladio-dev)

For professional support, please fill in our [contact form](http://www.palladio-simulator.com/about_palladio/support/).
