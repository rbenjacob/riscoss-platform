# Risk Data Format

Risk drivers are represented as one of three data types, `NUMBER`, `DISTRIBUTION`
and `EVIDENCE`.

### KPA BEGINS 12/19/2014
There is no such think as "risk data format". RISCOSS uses terminology of data elements, risk drivers, risk indicators, business risks, evidence. 

#### Data Elements
Raw data retrieved from various data sources, i.e. github development logs, fossology, email log, chat log
Data can be collected periodicatly or ad-hoc.
Data can be collected for any time windows: an entire year, one month etc.
Data can be stored "as is" within RISCOSS repositoy (versioning is required?) and/or its derived distribution lavels

#### Distribution of Risk Drivers
A Bayesian risk model is based on distribution of data values over several categories/groups/levels/discretizations (all synonimous for our purposes). The initial Bayesian setup process determins either statistically or by expert assessments what are the relevant groups for a risk driver. Than the raw data is analyzed resulting in distribution levels over the defined group levels. These distributios are used to create Bayesian networks that will become the Bayesian risk model.

Rules for setting up distributions (initial step performed off-line):

1. Raw data is summed up on a daily basis. E.g. collecting bug information or license blocking issues, data will be collected as "element per day," i.e. number of bugs per day.
3. Analyze bugs per day data and determine the distribution groups (typically 3 to 5 levels)
4. Calculate distributions of bugs per day over the distribution levels (akin to generating bar charts over predetermined bins)
5. Define relationships between risk drivers and risk indicators: either via expert opinion obtain in a tactical workshop or calculated if information is available
6. Define relationships between risk indicators and contextual indicators to business risks, by experts participating in a trategic workshop
7. Create the Bayesian model comprised of Bayesian networks for each risk indicator and one for business risk
8. Deploy Bayesian files (xdsl) into RISCOSS platform

Rules for running data collection to perform risk analysis:

1. Collect raw data  aggregated on daily basis
2. Calculate distributions of bugs per day over the predefined distribution levels (akin to generating bar charts over predetermined bins)
3. Distribution can represent single value, snap shot of OSS infor
4. Distribution can represent flactuations over time, i.e. the period for whcih data was collected
5. For the former: distribution over the Bayesian groups will be: 100% for the relevant level and 0% for all others
6. For the latter: distribution over the Bayesian groups will reflect different percentages of values (totaling 100%)

### KPA ENDS 12/19/2014

## NUMBER

`NUMBER` is a simple integer or decimal value based on it's meaning.

## DISTRIBUTION

A `DISTRIBUTION` is an array of decimal numbers, the sum of these numbers must be equal to one.
TODO: **I have no idea what the values of these numbers mean**

## EVIDENCE

An `EVIDENCE` data type is made up of six discrete components:

* **strength**: No idea what this is TODO
* **direction**: No idea what this is TODO
* **signal**: No idea what this is TODO
* **conflict**: No idea what this is TODO
* **negative**: No idea what this is TODO
* **positive**: No idea what this is TODO
