# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/native-lib.cpp )

add_library(
                IPOPT-lib
                SHARED
# Generated with the following command :
# find . -not -path '*/\.*' -type f -name '*.cpp' -not -iname '*native-lib.cpp' -o -name '*.hpp' -o -name '*.h' | sed 's|./app/src/|src/|'
            src/main/cpp/Common/config.h
            src/main/cpp/Common/config_ipopt_default.h
            src/main/cpp/Common/IpCachedResults.hpp
            src/main/cpp/Common/IpDebug.cpp
            src/main/cpp/Common/IpDebug.hpp
            src/main/cpp/Common/IpException.hpp
            src/main/cpp/Common/IpJournalist.cpp
            src/main/cpp/Common/IpJournalist.hpp
            src/main/cpp/Common/IpObserver.cpp
            src/main/cpp/Common/IpObserver.hpp
            src/main/cpp/Common/IpoptConfig.h
            src/main/cpp/Common/IpOptionsList.cpp
            src/main/cpp/Common/IpOptionsList.hpp
            src/main/cpp/Common/IpReferenced.hpp
            src/main/cpp/Common/IpRegOptions.cpp
            src/main/cpp/Common/IpRegOptions.hpp
            src/main/cpp/Common/IpSmartPtr.hpp
            src/main/cpp/Common/IpTaggedObject.cpp
            src/main/cpp/Common/IpTaggedObject.hpp
            src/main/cpp/Common/IpTimedTask.hpp
            src/main/cpp/Common/IpTypes.hpp
            src/main/cpp/Common/IpUtils.cpp
            src/main/cpp/Common/IpUtils.hpp
            src/main/cpp/LinAlg/IpBlas.cpp
            src/main/cpp/LinAlg/IpBlas.hpp
            src/main/cpp/LinAlg/IpCompoundMatrix.cpp
            src/main/cpp/LinAlg/IpCompoundMatrix.hpp
            src/main/cpp/LinAlg/IpCompoundSymMatrix.cpp
            src/main/cpp/LinAlg/IpCompoundSymMatrix.hpp
            src/main/cpp/LinAlg/IpCompoundVector.cpp
            src/main/cpp/LinAlg/IpCompoundVector.hpp
            src/main/cpp/LinAlg/IpDenseGenMatrix.cpp
            src/main/cpp/LinAlg/IpDenseGenMatrix.hpp
            src/main/cpp/LinAlg/IpDenseSymMatrix.cpp
            src/main/cpp/LinAlg/IpDenseSymMatrix.hpp
            src/main/cpp/LinAlg/IpDenseVector.cpp
            src/main/cpp/LinAlg/IpDenseVector.hpp
            src/main/cpp/LinAlg/IpDiagMatrix.cpp
            src/main/cpp/LinAlg/IpDiagMatrix.hpp
            src/main/cpp/LinAlg/IpExpandedMultiVectorMatrix.cpp
            src/main/cpp/LinAlg/IpExpandedMultiVectorMatrix.hpp
            src/main/cpp/LinAlg/IpExpansionMatrix.cpp
            src/main/cpp/LinAlg/IpExpansionMatrix.hpp
            src/main/cpp/LinAlg/IpIdentityMatrix.cpp
            src/main/cpp/LinAlg/IpIdentityMatrix.hpp
            src/main/cpp/LinAlg/IpLapack.cpp
            src/main/cpp/LinAlg/IpLapack.hpp
            src/main/cpp/LinAlg/IpLowRankUpdateSymMatrix.cpp
            src/main/cpp/LinAlg/IpLowRankUpdateSymMatrix.hpp
            src/main/cpp/LinAlg/IpMatrix.cpp
            src/main/cpp/LinAlg/IpMatrix.hpp
            src/main/cpp/LinAlg/IpMultiVectorMatrix.cpp
            src/main/cpp/LinAlg/IpMultiVectorMatrix.hpp
            src/main/cpp/LinAlg/IpScaledMatrix.cpp
            src/main/cpp/LinAlg/IpScaledMatrix.hpp
            src/main/cpp/LinAlg/IpSumMatrix.cpp
            src/main/cpp/LinAlg/IpSumMatrix.hpp
            src/main/cpp/LinAlg/IpSumSymMatrix.cpp
            src/main/cpp/LinAlg/IpSumSymMatrix.hpp
            src/main/cpp/LinAlg/IpSymMatrix.hpp
            src/main/cpp/LinAlg/IpSymScaledMatrix.cpp
            src/main/cpp/LinAlg/IpSymScaledMatrix.hpp
            src/main/cpp/LinAlg/IpTransposeMatrix.cpp
            src/main/cpp/LinAlg/IpTransposeMatrix.hpp
            src/main/cpp/LinAlg/IpVector.cpp
            src/main/cpp/LinAlg/IpVector.hpp
            src/main/cpp/LinAlg/IpZeroMatrix.cpp
            src/main/cpp/LinAlg/IpZeroMatrix.hpp
            src/main/cpp/LinAlg/IpZeroSymMatrix.cpp
            src/main/cpp/LinAlg/IpZeroSymMatrix.hpp
            src/main/cpp/LinAlg/TMatrices/IpGenTMatrix.cpp
            src/main/cpp/LinAlg/TMatrices/IpGenTMatrix.hpp
            src/main/cpp/LinAlg/TMatrices/IpSymTMatrix.cpp
            src/main/cpp/LinAlg/TMatrices/IpSymTMatrix.hpp
            src/main/cpp/LinAlg/TMatrices/IpTripletHelper.cpp
            src/main/cpp/LinAlg/TMatrices/IpTripletHelper.hpp
            src/main/cpp/Algorithm/IpAdaptiveMuUpdate.cpp
            src/main/cpp/Algorithm/IpAdaptiveMuUpdate.hpp
            src/main/cpp/Algorithm/IpAlgBuilder.cpp
            src/main/cpp/Algorithm/IpAlgBuilder.hpp
            src/main/cpp/Algorithm/IpAlgorithmRegOp.cpp
            src/main/cpp/Algorithm/IpAlgorithmRegOp.hpp
            src/main/cpp/Algorithm/IpAlgStrategy.hpp
            src/main/cpp/Algorithm/IpAugRestoSystemSolver.cpp
            src/main/cpp/Algorithm/IpAugRestoSystemSolver.hpp
            src/main/cpp/Algorithm/IpAugSystemSolver.hpp
            src/main/cpp/Algorithm/IpBacktrackingLineSearch.cpp
            src/main/cpp/Algorithm/IpBacktrackingLineSearch.hpp
            src/main/cpp/Algorithm/IpBacktrackingLSAcceptor.hpp
            src/main/cpp/Algorithm/IpConvCheck.hpp
            src/main/cpp/Algorithm/IpDefaultIterateInitializer.cpp
            src/main/cpp/Algorithm/IpDefaultIterateInitializer.hpp
            src/main/cpp/Algorithm/IpEqMultCalculator.hpp
            src/main/cpp/Algorithm/IpEquilibrationScaling.cpp
            src/main/cpp/Algorithm/IpEquilibrationScaling.hpp
            src/main/cpp/Algorithm/IpExactHessianUpdater.cpp
            src/main/cpp/Algorithm/IpExactHessianUpdater.hpp
            src/main/cpp/Algorithm/IpFilter.cpp
            src/main/cpp/Algorithm/IpFilter.hpp
            src/main/cpp/Algorithm/IpFilterLSAcceptor.cpp
            src/main/cpp/Algorithm/IpFilterLSAcceptor.hpp
            src/main/cpp/Algorithm/IpGenAugSystemSolver.cpp
            src/main/cpp/Algorithm/IpGenAugSystemSolver.hpp
            src/main/cpp/Algorithm/IpGradientScaling.cpp
            src/main/cpp/Algorithm/IpGradientScaling.hpp
            src/main/cpp/Algorithm/IpHessianUpdater.hpp
            src/main/cpp/Algorithm/IpIpoptAlg.cpp
            src/main/cpp/Algorithm/IpIpoptAlg.hpp
            src/main/cpp/Algorithm/IpIpoptCalculatedQuantities.cpp
            src/main/cpp/Algorithm/IpIpoptCalculatedQuantities.hpp
            src/main/cpp/Algorithm/IpIpoptData.cpp
            src/main/cpp/Algorithm/IpIpoptData.hpp
            src/main/cpp/Algorithm/IpIpoptNLP.hpp
            src/main/cpp/Algorithm/IpIterateInitializer.hpp
            src/main/cpp/Algorithm/IpIteratesVector.cpp
            src/main/cpp/Algorithm/IpIteratesVector.hpp
            src/main/cpp/Algorithm/IpIterationOutput.hpp
            src/main/cpp/Algorithm/IpLeastSquareMults.cpp
            src/main/cpp/Algorithm/IpLeastSquareMults.hpp
            src/main/cpp/Algorithm/IpLimMemQuasiNewtonUpdater.cpp
            src/main/cpp/Algorithm/IpLimMemQuasiNewtonUpdater.hpp
            src/main/cpp/Algorithm/IpLineSearch.hpp
            src/main/cpp/Algorithm/IpLoqoMuOracle.cpp
            src/main/cpp/Algorithm/IpLoqoMuOracle.hpp
            src/main/cpp/Algorithm/IpLowRankAugSystemSolver.cpp
            src/main/cpp/Algorithm/IpLowRankAugSystemSolver.hpp
            src/main/cpp/Algorithm/IpLowRankSSAugSystemSolver.cpp
            src/main/cpp/Algorithm/IpLowRankSSAugSystemSolver.hpp
            src/main/cpp/Algorithm/IpMonotoneMuUpdate.cpp
            src/main/cpp/Algorithm/IpMonotoneMuUpdate.hpp
            src/main/cpp/Algorithm/IpMuOracle.hpp
            src/main/cpp/Algorithm/IpMuUpdate.hpp
            src/main/cpp/Algorithm/IpNLPBoundsRemover.cpp
            src/main/cpp/Algorithm/IpNLPBoundsRemover.hpp
            src/main/cpp/Algorithm/IpNLPScaling.cpp
            src/main/cpp/Algorithm/IpNLPScaling.hpp
            src/main/cpp/Algorithm/IpOptErrorConvCheck.cpp
            src/main/cpp/Algorithm/IpOptErrorConvCheck.hpp
            src/main/cpp/Algorithm/IpOrigIpoptNLP.cpp
            src/main/cpp/Algorithm/IpOrigIpoptNLP.hpp
            src/main/cpp/Algorithm/IpOrigIterationOutput.cpp
            src/main/cpp/Algorithm/IpOrigIterationOutput.hpp
            src/main/cpp/Algorithm/IpPDFullSpaceSolver.cpp
            src/main/cpp/Algorithm/IpPDFullSpaceSolver.hpp
            src/main/cpp/Algorithm/IpPDPerturbationHandler.cpp
            src/main/cpp/Algorithm/IpPDPerturbationHandler.hpp
            src/main/cpp/Algorithm/IpPDSearchDirCalc.cpp
            src/main/cpp/Algorithm/IpPDSearchDirCalc.hpp
            src/main/cpp/Algorithm/IpPDSystemSolver.hpp
            src/main/cpp/Algorithm/IpPenaltyLSAcceptor.cpp
            src/main/cpp/Algorithm/IpPenaltyLSAcceptor.hpp
            src/main/cpp/Algorithm/IpProbingMuOracle.cpp
            src/main/cpp/Algorithm/IpProbingMuOracle.hpp
            src/main/cpp/Algorithm/IpQualityFunctionMuOracle.cpp
            src/main/cpp/Algorithm/IpQualityFunctionMuOracle.hpp
            src/main/cpp/Algorithm/IpRestoConvCheck.cpp
            src/main/cpp/Algorithm/IpRestoConvCheck.hpp
            src/main/cpp/Algorithm/IpRestoFilterConvCheck.cpp
            src/main/cpp/Algorithm/IpRestoFilterConvCheck.hpp
            src/main/cpp/Algorithm/IpRestoIpoptNLP.cpp
            src/main/cpp/Algorithm/IpRestoIpoptNLP.hpp
            src/main/cpp/Algorithm/IpRestoIterateInitializer.cpp
            src/main/cpp/Algorithm/IpRestoIterateInitializer.hpp
            src/main/cpp/Algorithm/IpRestoIterationOutput.cpp
            src/main/cpp/Algorithm/IpRestoIterationOutput.hpp
            src/main/cpp/Algorithm/IpRestoMinC_1Nrm.cpp
            src/main/cpp/Algorithm/IpRestoMinC_1Nrm.hpp
            src/main/cpp/Algorithm/IpRestoPenaltyConvCheck.cpp
            src/main/cpp/Algorithm/IpRestoPenaltyConvCheck.hpp
            src/main/cpp/Algorithm/IpRestoPhase.hpp
            src/main/cpp/Algorithm/IpRestoRestoPhase.cpp
            src/main/cpp/Algorithm/IpRestoRestoPhase.hpp
            src/main/cpp/Algorithm/IpSearchDirCalculator.hpp
            src/main/cpp/Algorithm/IpStdAugSystemSolver.cpp
            src/main/cpp/Algorithm/IpStdAugSystemSolver.hpp
            src/main/cpp/Algorithm/IpTimingStatistics.cpp
            src/main/cpp/Algorithm/IpTimingStatistics.hpp
            src/main/cpp/Algorithm/IpUserScaling.cpp
            src/main/cpp/Algorithm/IpUserScaling.hpp
            src/main/cpp/Algorithm/IpWarmStartIterateInitializer.cpp
            src/main/cpp/Algorithm/IpWarmStartIterateInitializer.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactAlgBuilder.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactAlgBuilder.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactCq.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactCq.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactData.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactData.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactDoglegNormal.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactDoglegNormal.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactLSAcceptor.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactLSAcceptor.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactNewtonNormal.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactNewtonNormal.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactNormalStepCalc.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactNormalTerminationTester.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactNormalTerminationTester.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactPDSolver.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactPDSolver.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactPDTerminationTester.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactPDTerminationTester.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactRegOp.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactRegOp.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactSearchDirCalc.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactSearchDirCalc.hpp
            src/main/cpp/Algorithm/Inexact/IpInexactTSymScalingMethod.cpp
            src/main/cpp/Algorithm/Inexact/IpInexactTSymScalingMethod.hpp
            src/main/cpp/Algorithm/Inexact/IpIterativePardisoSolverInterface.cpp
            src/main/cpp/Algorithm/Inexact/IpIterativePardisoSolverInterface.hpp
            src/main/cpp/Algorithm/Inexact/IpIterativeSolverTerminationTester.cpp
            src/main/cpp/Algorithm/Inexact/IpIterativeSolverTerminationTester.hpp
            src/main/cpp/Algorithm/LinearSolvers/hsl_ma77d.h
            src/main/cpp/Algorithm/LinearSolvers/hsl_ma86d.h
            src/main/cpp/Algorithm/LinearSolvers/hsl_ma97d.h
            src/main/cpp/Algorithm/LinearSolvers/hsl_mc68i.h
            src/main/cpp/Algorithm/LinearSolvers/IpGenKKTSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpIterativeWsmpSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpIterativeWsmpSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpLinearSolversRegOp.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpLinearSolversRegOp.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa27TSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa27TSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa28TDependencyDetector.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa28TDependencyDetector.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa57TSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa57TSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa77SolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa77SolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa86SolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa86SolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa97SolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMa97SolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMc19TSymScalingMethod.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMc19TSymScalingMethod.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpMumpsSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpMumpsSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpPardisoSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpPardisoSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpSlackBasedTSymScalingMethod.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpSlackBasedTSymScalingMethod.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpSparseSymLinearSolverInterface.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpSymLinearSolver.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpTDependencyDetector.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpTripletToCSRConverter.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpTripletToCSRConverter.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpTSymDependencyDetector.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpTSymDependencyDetector.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpTSymLinearSolver.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpTSymLinearSolver.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpTSymScalingMethod.hpp
            src/main/cpp/Algorithm/LinearSolvers/IpWsmpSolverInterface.cpp
            src/main/cpp/Algorithm/LinearSolvers/IpWsmpSolverInterface.hpp
            src/main/cpp/Apps/AmplSolver/ampl_ipopt.cpp
            src/main/cpp/Apps/AmplSolver/AmplTNLP.cpp
            src/main/cpp/Apps/AmplSolver/AmplTNLP.hpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyCq.cpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyCq.hpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyData.cpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyData.hpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyLSAcceptor.cpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyLSAcceptor.hpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyRegOp.cpp
            src/main/cpp/contrib/CGPenalty/IpCGPenaltyRegOp.hpp
            src/main/cpp/contrib/CGPenalty/IpCGPerturbationHandler.cpp
            src/main/cpp/contrib/CGPenalty/IpCGPerturbationHandler.hpp
            src/main/cpp/contrib/CGPenalty/IpCGSearchDirCalc.cpp
            src/main/cpp/contrib/CGPenalty/IpCGSearchDirCalc.hpp
            src/main/cpp/contrib/CGPenalty/IpPiecewisePenalty.cpp
            src/main/cpp/contrib/CGPenalty/IpPiecewisePenalty.hpp
            src/main/cpp/contrib/LinearSolverLoader/HSLLoader.h
            src/main/cpp/contrib/LinearSolverLoader/LibraryHandler.h
            src/main/cpp/contrib/LinearSolverLoader/PardisoLoader.h
            src/main/cpp/Interfaces/IpAlgTypes.hpp
            src/main/cpp/Interfaces/IpInterfacesRegOp.cpp
            src/main/cpp/Interfaces/IpInterfacesRegOp.hpp
            src/main/cpp/Interfaces/IpIpoptApplication.cpp
            src/main/cpp/Interfaces/IpIpoptApplication.hpp
            src/main/cpp/Interfaces/IpNLP.hpp
            src/main/cpp/Interfaces/IpReturnCodes.h
            src/main/cpp/Interfaces/IpReturnCodes.hpp
            src/main/cpp/Interfaces/IpReturnCodes_inc.h
            src/main/cpp/Interfaces/IpSolveStatistics.cpp
            src/main/cpp/Interfaces/IpSolveStatistics.hpp
            src/main/cpp/Interfaces/IpStdCInterface.cpp
            src/main/cpp/Interfaces/IpStdCInterface.h
            src/main/cpp/Interfaces/IpStdInterfaceTNLP.cpp
            src/main/cpp/Interfaces/IpStdInterfaceTNLP.hpp
            src/main/cpp/Interfaces/IpTNLP.hpp
            src/main/cpp/Interfaces/IpTNLPAdapter.cpp
            src/main/cpp/Interfaces/IpTNLPAdapter.hpp
            src/main/cpp/Interfaces/IpTNLPReducer.cpp
            src/main/cpp/Interfaces/IpTNLPReducer.hpp
           )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib
                       IPOPT-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
