using System;
using System.Runtime.Serialization;

namespace HabboWasm.Unity.Exceptions
{
    public class UnityException : Exception
    {
        public UnityException()
        {
        }

        protected UnityException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }

        public UnityException(string message) : base(message)
        {
        }

        public UnityException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}