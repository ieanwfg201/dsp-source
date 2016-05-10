from abc import ABCMeta, abstractmethod

class GenericForecastingAlgorithm(object) :
    """
    Interface for the generic forecasting algorithm. A concrete
    implementation has to implement the method createAndDumpForecast
    to be consumed by the forecasting framework
    """
    __metaclass__ = ABCMeta

    @abstractmethod
    def createAndDumpForecast(self, cfgParams, logAdapater) :
        pass
