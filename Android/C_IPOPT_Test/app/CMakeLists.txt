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

            src/main/cpp/config_default.h
            src/main/cpp/config_ipopt_default.h
            src/main/cpp/ampl_ipopt.cpp
            src/main/cpp/AmplTNLP.cpp
            src/main/cpp/AmplTNLP.hpp
            src/main/cpp/asl.h
            src/main/cpp/asl_pfgh.h
            src/main/cpp/funcadd.h
            src/main/cpp/getstub.h
            src/main/cpp/hsl_ma77d.h
            src/main/cpp/hsl_ma86d.h
            src/main/cpp/hsl_ma97d.h
            src/main/cpp/hsl_mc68i.h
            src/main/cpp/HSLLoader.h
            src/main/cpp/IpAdaptiveMuUpdate.cpp
            src/main/cpp/IpAdaptiveMuUpdate.hpp
            src/main/cpp/IpAlgBuilder.cpp
            src/main/cpp/IpAlgBuilder.hpp
            src/main/cpp/IpAlgorithmRegOp.cpp
            src/main/cpp/IpAlgorithmRegOp.hpp
            src/main/cpp/IpAlgStrategy.hpp
            src/main/cpp/IpAlgTypes.hpp
            src/main/cpp/IpAugRestoSystemSolver.cpp
            src/main/cpp/IpAugRestoSystemSolver.hpp
            src/main/cpp/IpAugSystemSolver.hpp
            src/main/cpp/IpBacktrackingLineSearch.cpp
            src/main/cpp/IpBacktrackingLineSearch.hpp
            src/main/cpp/IpBacktrackingLSAcceptor.hpp
            src/main/cpp/IpBlas.cpp
            src/main/cpp/IpBlas.hpp
            src/main/cpp/IpCachedResults.hpp
            src/main/cpp/IpCGPenaltyCq.cpp
            src/main/cpp/IpCGPenaltyCq.hpp
            src/main/cpp/IpCGPenaltyData.cpp
            src/main/cpp/IpCGPenaltyData.hpp
            src/main/cpp/IpCGPenaltyLSAcceptor.cpp
            src/main/cpp/IpCGPenaltyLSAcceptor.hpp
            src/main/cpp/IpCGPenaltyRegOp.cpp
            src/main/cpp/IpCGPenaltyRegOp.hpp
            src/main/cpp/IpCGPerturbationHandler.cpp
            src/main/cpp/IpCGPerturbationHandler.hpp
            src/main/cpp/IpCGSearchDirCalc.cpp
            src/main/cpp/IpCGSearchDirCalc.hpp
            src/main/cpp/IpCompoundMatrix.cpp
            src/main/cpp/IpCompoundMatrix.hpp
            src/main/cpp/IpCompoundSymMatrix.cpp
            src/main/cpp/IpCompoundSymMatrix.hpp
            src/main/cpp/IpCompoundVector.cpp
            src/main/cpp/IpCompoundVector.hpp
            src/main/cpp/IpConvCheck.hpp
            src/main/cpp/IpDebug.cpp
            src/main/cpp/IpDebug.hpp
            src/main/cpp/IpDefaultIterateInitializer.cpp
            src/main/cpp/IpDefaultIterateInitializer.hpp
            src/main/cpp/IpDenseGenMatrix.cpp
            src/main/cpp/IpDenseGenMatrix.hpp
            src/main/cpp/IpDenseSymMatrix.cpp
            src/main/cpp/IpDenseSymMatrix.hpp
            src/main/cpp/IpDenseVector.cpp
            src/main/cpp/IpDenseVector.hpp
            src/main/cpp/IpDiagMatrix.cpp
            src/main/cpp/IpDiagMatrix.hpp
            src/main/cpp/IpEqMultCalculator.hpp
            src/main/cpp/IpEquilibrationScaling.cpp
            src/main/cpp/IpEquilibrationScaling.hpp
            src/main/cpp/IpExactHessianUpdater.cpp
            src/main/cpp/IpExactHessianUpdater.hpp
            src/main/cpp/IpException.hpp
            src/main/cpp/IpExpandedMultiVectorMatrix.cpp
            src/main/cpp/IpExpandedMultiVectorMatrix.hpp
            src/main/cpp/IpExpansionMatrix.cpp
            src/main/cpp/IpExpansionMatrix.hpp
            src/main/cpp/IpFilter.cpp
            src/main/cpp/IpFilter.hpp
            src/main/cpp/IpFilterLSAcceptor.cpp
            src/main/cpp/IpFilterLSAcceptor.hpp
            src/main/cpp/IpGenAugSystemSolver.cpp
            src/main/cpp/IpGenAugSystemSolver.hpp
            src/main/cpp/IpGenKKTSolverInterface.hpp
            src/main/cpp/IpGenTMatrix.cpp
            src/main/cpp/IpGenTMatrix.hpp
            src/main/cpp/IpGradientScaling.cpp
            src/main/cpp/IpGradientScaling.hpp
            src/main/cpp/IpHessianUpdater.hpp
            src/main/cpp/IpIdentityMatrix.cpp
            src/main/cpp/IpIdentityMatrix.hpp
            src/main/cpp/IpInexactAlgBuilder.cpp
            src/main/cpp/IpInexactAlgBuilder.hpp
            src/main/cpp/IpInexactCq.cpp
            src/main/cpp/IpInexactCq.hpp
            src/main/cpp/IpInexactData.cpp
            src/main/cpp/IpInexactData.hpp
            src/main/cpp/IpInexactDoglegNormal.cpp
            src/main/cpp/IpInexactDoglegNormal.hpp
            src/main/cpp/IpInexactLSAcceptor.cpp
            src/main/cpp/IpInexactLSAcceptor.hpp
            src/main/cpp/IpInexactNewtonNormal.cpp
            src/main/cpp/IpInexactNewtonNormal.hpp
            src/main/cpp/IpInexactNormalStepCalc.hpp
            src/main/cpp/IpInexactNormalTerminationTester.cpp
            src/main/cpp/IpInexactNormalTerminationTester.hpp
            src/main/cpp/IpInexactPDSolver.cpp
            src/main/cpp/IpInexactPDSolver.hpp
            src/main/cpp/IpInexactPDTerminationTester.cpp
            src/main/cpp/IpInexactPDTerminationTester.hpp
            src/main/cpp/IpInexactRegOp.cpp
            src/main/cpp/IpInexactRegOp.hpp
            src/main/cpp/IpInexactSearchDirCalc.cpp
            src/main/cpp/IpInexactSearchDirCalc.hpp
            src/main/cpp/IpInexactTSymScalingMethod.cpp
            src/main/cpp/IpInexactTSymScalingMethod.hpp
            src/main/cpp/IpInterfacesRegOp.cpp
            src/main/cpp/IpInterfacesRegOp.hpp
            src/main/cpp/IpIpoptAlg.cpp
            src/main/cpp/IpIpoptAlg.hpp
            src/main/cpp/IpIpoptApplication.cpp
            src/main/cpp/IpIpoptApplication.hpp
            src/main/cpp/IpIpoptCalculatedQuantities.cpp
            src/main/cpp/IpIpoptCalculatedQuantities.hpp
            src/main/cpp/IpIpoptData.cpp
            src/main/cpp/IpIpoptData.hpp
            src/main/cpp/IpIpoptNLP.hpp
            src/main/cpp/IpIterateInitializer.hpp
            src/main/cpp/IpIteratesVector.cpp
            src/main/cpp/IpIteratesVector.hpp
            src/main/cpp/IpIterationOutput.hpp
            src/main/cpp/IpIterativePardisoSolverInterface.cpp
            src/main/cpp/IpIterativePardisoSolverInterface.hpp
            src/main/cpp/IpIterativeSolverTerminationTester.cpp
            src/main/cpp/IpIterativeSolverTerminationTester.hpp
            src/main/cpp/IpIterativeWsmpSolverInterface.cpp
            src/main/cpp/IpIterativeWsmpSolverInterface.hpp
            src/main/cpp/IpJournalist.cpp
            src/main/cpp/IpJournalist.hpp
            src/main/cpp/IpLapack.cpp
            src/main/cpp/IpLapack.hpp
            src/main/cpp/IpLeastSquareMults.cpp
            src/main/cpp/IpLeastSquareMults.hpp
            src/main/cpp/IpLimMemQuasiNewtonUpdater.cpp
            src/main/cpp/IpLimMemQuasiNewtonUpdater.hpp
            src/main/cpp/IpLinearSolversRegOp.cpp
            src/main/cpp/IpLinearSolversRegOp.hpp
            src/main/cpp/IpLineSearch.hpp
            src/main/cpp/IpLoqoMuOracle.cpp
            src/main/cpp/IpLoqoMuOracle.hpp
            src/main/cpp/IpLowRankAugSystemSolver.cpp
            src/main/cpp/IpLowRankAugSystemSolver.hpp
            src/main/cpp/IpLowRankSSAugSystemSolver.cpp
            src/main/cpp/IpLowRankSSAugSystemSolver.hpp
            src/main/cpp/IpLowRankUpdateSymMatrix.cpp
            src/main/cpp/IpLowRankUpdateSymMatrix.hpp
            src/main/cpp/IpMa27TSolverInterface.cpp
            src/main/cpp/IpMa27TSolverInterface.hpp
            src/main/cpp/IpMa28TDependencyDetector.cpp
            src/main/cpp/IpMa28TDependencyDetector.hpp
            src/main/cpp/IpMa57TSolverInterface.cpp
            src/main/cpp/IpMa57TSolverInterface.hpp
            src/main/cpp/IpMa77SolverInterface.cpp
            src/main/cpp/IpMa77SolverInterface.hpp
            src/main/cpp/IpMa86SolverInterface.cpp
            src/main/cpp/IpMa86SolverInterface.hpp
            src/main/cpp/IpMa97SolverInterface.cpp
            src/main/cpp/IpMa97SolverInterface.hpp
            src/main/cpp/IpMatrix.cpp
            src/main/cpp/IpMatrix.hpp
            src/main/cpp/IpMc19TSymScalingMethod.cpp
            src/main/cpp/IpMc19TSymScalingMethod.hpp
            src/main/cpp/IpMonotoneMuUpdate.cpp
            src/main/cpp/IpMonotoneMuUpdate.hpp
            src/main/cpp/IpMultiVectorMatrix.cpp
            src/main/cpp/IpMultiVectorMatrix.hpp
            src/main/cpp/IpMumpsSolverInterface.cpp
            src/main/cpp/IpMumpsSolverInterface.hpp
            src/main/cpp/IpMuOracle.hpp
            src/main/cpp/IpMuUpdate.hpp
            src/main/cpp/IpNLP.hpp
            src/main/cpp/IpNLPBoundsRemover.cpp
            src/main/cpp/IpNLPBoundsRemover.hpp
            src/main/cpp/IpNLPScaling.cpp
            src/main/cpp/IpNLPScaling.hpp
            src/main/cpp/IpObserver.cpp
            src/main/cpp/IpObserver.hpp
            src/main/cpp/IpoptConfig.h
            src/main/cpp/IpOptErrorConvCheck.cpp
            src/main/cpp/IpOptErrorConvCheck.hpp
            src/main/cpp/IpOptionsList.cpp
            src/main/cpp/IpOptionsList.hpp
            src/main/cpp/IpOrigIpoptNLP.cpp
            src/main/cpp/IpOrigIpoptNLP.hpp
            src/main/cpp/IpOrigIterationOutput.cpp
            src/main/cpp/IpOrigIterationOutput.hpp
            src/main/cpp/IpPardisoSolverInterface.cpp
            src/main/cpp/IpPardisoSolverInterface.hpp
            src/main/cpp/IpPDFullSpaceSolver.cpp
            src/main/cpp/IpPDFullSpaceSolver.hpp
            src/main/cpp/IpPDPerturbationHandler.cpp
            src/main/cpp/IpPDPerturbationHandler.hpp
            src/main/cpp/IpPDSearchDirCalc.cpp
            src/main/cpp/IpPDSearchDirCalc.hpp
            src/main/cpp/IpPDSystemSolver.hpp
            src/main/cpp/IpPenaltyLSAcceptor.cpp
            src/main/cpp/IpPenaltyLSAcceptor.hpp
            src/main/cpp/IpPiecewisePenalty.cpp
            src/main/cpp/IpPiecewisePenalty.hpp
            src/main/cpp/IpProbingMuOracle.cpp
            src/main/cpp/IpProbingMuOracle.hpp
            src/main/cpp/IpQualityFunctionMuOracle.cpp
            src/main/cpp/IpQualityFunctionMuOracle.hpp
            src/main/cpp/IpReferenced.hpp
            src/main/cpp/IpRegOptions.cpp
            src/main/cpp/IpRegOptions.hpp
            src/main/cpp/IpRestoConvCheck.cpp
            src/main/cpp/IpRestoConvCheck.hpp
            src/main/cpp/IpRestoFilterConvCheck.cpp
            src/main/cpp/IpRestoFilterConvCheck.hpp
            src/main/cpp/IpRestoIpoptNLP.cpp
            src/main/cpp/IpRestoIpoptNLP.hpp
            src/main/cpp/IpRestoIterateInitializer.cpp
            src/main/cpp/IpRestoIterateInitializer.hpp
            src/main/cpp/IpRestoIterationOutput.cpp
            src/main/cpp/IpRestoIterationOutput.hpp
            src/main/cpp/IpRestoMinC_1Nrm.cpp
            src/main/cpp/IpRestoMinC_1Nrm.hpp
            src/main/cpp/IpRestoPenaltyConvCheck.cpp
            src/main/cpp/IpRestoPenaltyConvCheck.hpp
            src/main/cpp/IpRestoPhase.hpp
            src/main/cpp/IpRestoRestoPhase.cpp
            src/main/cpp/IpRestoRestoPhase.hpp
            src/main/cpp/IpReturnCodes.h
            src/main/cpp/IpReturnCodes.hpp
            src/main/cpp/IpReturnCodes_inc.h
            src/main/cpp/IpScaledMatrix.cpp
            src/main/cpp/IpScaledMatrix.hpp
            src/main/cpp/IpSearchDirCalculator.hpp
            src/main/cpp/IpSlackBasedTSymScalingMethod.cpp
            src/main/cpp/IpSlackBasedTSymScalingMethod.hpp
            src/main/cpp/IpSmartPtr.hpp
            src/main/cpp/IpSolveStatistics.cpp
            src/main/cpp/IpSolveStatistics.hpp
            src/main/cpp/IpSparseSymLinearSolverInterface.hpp
            src/main/cpp/IpStdAugSystemSolver.cpp
            src/main/cpp/IpStdAugSystemSolver.hpp
            src/main/cpp/IpStdCInterface.cpp
            src/main/cpp/IpStdCInterface.h
            src/main/cpp/IpStdInterfaceTNLP.cpp
            src/main/cpp/IpStdInterfaceTNLP.hpp
            src/main/cpp/IpSumMatrix.cpp
            src/main/cpp/IpSumMatrix.hpp
            src/main/cpp/IpSumSymMatrix.cpp
            src/main/cpp/IpSumSymMatrix.hpp
            src/main/cpp/IpSymLinearSolver.hpp
            src/main/cpp/IpSymMatrix.hpp
            src/main/cpp/IpSymScaledMatrix.cpp
            src/main/cpp/IpSymScaledMatrix.hpp
            src/main/cpp/IpSymTMatrix.cpp
            src/main/cpp/IpSymTMatrix.hpp
            src/main/cpp/IpTaggedObject.cpp
            src/main/cpp/IpTaggedObject.hpp
            src/main/cpp/IpTDependencyDetector.hpp
            src/main/cpp/IpTimedTask.hpp
            src/main/cpp/IpTimingStatistics.cpp
            src/main/cpp/IpTimingStatistics.hpp
            src/main/cpp/IpTNLP.hpp
            src/main/cpp/IpTNLPAdapter.cpp
            src/main/cpp/IpTNLPAdapter.hpp
            src/main/cpp/IpTNLPReducer.cpp
            src/main/cpp/IpTNLPReducer.hpp
            src/main/cpp/IpTransposeMatrix.cpp
            src/main/cpp/IpTransposeMatrix.hpp
            src/main/cpp/IpTripletHelper.cpp
            src/main/cpp/IpTripletHelper.hpp
            src/main/cpp/IpTripletToCSRConverter.cpp
            src/main/cpp/IpTripletToCSRConverter.hpp
            src/main/cpp/IpTSymDependencyDetector.cpp
            src/main/cpp/IpTSymDependencyDetector.hpp
            src/main/cpp/IpTSymLinearSolver.cpp
            src/main/cpp/IpTSymLinearSolver.hpp
            src/main/cpp/IpTSymScalingMethod.hpp
            src/main/cpp/IpTypes.hpp
            src/main/cpp/IpUserScaling.cpp
            src/main/cpp/IpUserScaling.hpp
            src/main/cpp/IpUtils.cpp
            src/main/cpp/IpUtils.hpp
            src/main/cpp/IpVector.cpp
            src/main/cpp/IpVector.hpp
            src/main/cpp/IpWarmStartIterateInitializer.cpp
            src/main/cpp/IpWarmStartIterateInitializer.hpp
            src/main/cpp/IpWsmpSolverInterface.cpp
            src/main/cpp/IpWsmpSolverInterface.hpp
            src/main/cpp/IpZeroMatrix.cpp
            src/main/cpp/IpZeroMatrix.hpp
            src/main/cpp/IpZeroSymMatrix.cpp
            src/main/cpp/IpZeroSymMatrix.hpp
            src/main/cpp/LibraryHandler.h
            src/main/cpp/native-lib.cpp
            src/main/cpp/nlp.h
            src/main/cpp/nlp2.h
            src/main/cpp/PardisoLoader.h
            src/main/cpp/psinfo.h
            src/main/cpp/stdio1.h
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
